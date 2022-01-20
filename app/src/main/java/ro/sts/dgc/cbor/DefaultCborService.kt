package ro.sts.dgc.cbor

import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.dataformat.cbor.databind.CBORMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import ro.sts.dgc.model.ContentType
import ro.sts.dgc.model.VerificationResult
import ro.sts.dgc.schema.payload.v1.Eudcc
import java.time.Instant
import java.time.LocalDate


/**
 * Encodes/decodes input as a CBOR structure
 */
open class DefaultCborService : CborService {

    override fun encode(input: Eudcc): ByteArray {
        return CBORMapper().registerModule(JavaTimeModule()).writeValueAsBytes(input)
    }

    override fun decode(input: ByteArray, verificationResult: VerificationResult): Eudcc {
        verificationResult.cborDecoded = false
        try {
            val timeModule: SimpleModule = JavaTimeModule()
            // TODO combine these 2
            timeModule.addDeserializer(Instant::class.java, LenientInstantDeserializer())
            timeModule.addDeserializer(Instant::class.java, CustomInstantDeserializer.INSTANT);
            timeModule.addDeserializer(LocalDate::class.java, LenientLocalDateDeserializer())

            return CBORMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .setSerializationInclusion(Include.NON_NULL)
                .setSerializationInclusion(Include.NON_EMPTY)
                .registerModule(timeModule)
                .readValue(input, Eudcc::class.java)
                .also { result ->
                    verificationResult.cborDecoded = true
                    if (result.t?.filterNotNull()?.isNotEmpty() == true) {
                        verificationResult.contentType.add(ContentType.TEST)
                    }
                    if (result.v?.filterNotNull()?.isNotEmpty() == true) {
                        verificationResult.contentType.add(ContentType.VACCINATION)
                    }
                    if (result.r?.filterNotNull()?.isNotEmpty() == true) {
                        verificationResult.contentType.add(ContentType.RECOVERY)
                    }
                }
        } catch (e: Throwable) {
            return Eudcc()
        }
    }

}