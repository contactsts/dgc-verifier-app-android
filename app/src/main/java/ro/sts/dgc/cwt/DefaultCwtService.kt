package ro.sts.dgc.cwt

import com.upokecenter.cbor.CBORObject
import ro.sts.dgc.model.VerificationResult
import java.time.Clock
import java.time.Duration
import java.time.Instant


/**
 * Encodes/decodes input as a CWT structure, ready to sign with COSE
 */
open class DefaultCwtService(
    private val countryCode: String = "RO",
    private val validity: Duration = Duration.ofHours(48),
    private val clock: Clock = Clock.systemUTC(),
) : CwtService {

    private val keyEuDgcV1 = CBORObject.FromObject(1)

    override fun encode(input: ByteArray): ByteArray {
        val issueTime = clock.instant()
        val expirationTime = issueTime + validity
        return CBORObject.NewMap().also {
            it[CwtHeaderKeys.ISSUER.asCBOR()] = CBORObject.FromObject(countryCode)
            it[CwtHeaderKeys.ISSUED_AT.asCBOR()] = CBORObject.FromObject(issueTime.epochSecond)
            it[CwtHeaderKeys.EXPIRATION.asCBOR()] = CBORObject.FromObject(expirationTime.epochSecond)
            it[CwtHeaderKeys.HCERT.asCBOR()] = CBORObject.NewMap().also { hcert ->
                try {
                    hcert[keyEuDgcV1] = CBORObject.DecodeFromBytes(input)
                } catch (e: Throwable) {
                    hcert[keyEuDgcV1] = CBORObject.FromObject(input)
                }
            }
        }.EncodeToBytes()
    }

    override fun decode(input: ByteArray, verificationResult: VerificationResult): ByteArray {
        verificationResult.cwtDecoded = false
        try {
            val map = CBORObject.DecodeFromBytes(input)

            map[CwtHeaderKeys.ISSUER.asCBOR()]?.let {
                verificationResult.issuer = it.AsString()
            }
            map[CwtHeaderKeys.ISSUED_AT.asCBOR()]?.let {
                verificationResult.issuedAt = Instant.ofEpochSecond(it.AsInt64())
            }
            map[CwtHeaderKeys.EXPIRATION.asCBOR()]?.let {
                verificationResult.expirationTime = Instant.ofEpochSecond(it.AsInt64())
            }

            map[CwtHeaderKeys.HCERT.asCBOR()]?.let { hcert -> // SPEC
                hcert[keyEuDgcV1]?.let { eudgcV1 ->
                    return getContents(eudgcV1).also {
                        verificationResult.cwtDecoded = true
                        verificationResult.hcertJson = eudgcV1.ToJSONString()
                    }
                }
            }
            return input
        } catch (e: Throwable) {
            return input
        }
    }

    private fun getContents(it: CBORObject) = try {
        it.GetByteString()
    } catch (e: Throwable) {
        it.EncodeToBytes()
    }

}