package ro.sts.dgc.cose

import COSE.HeaderKeys
import COSE.MessageTag
import COSE.Sign1Message
import org.bouncycastle.jce.provider.BouncyCastleProvider
import ro.sts.dgc.asBase64
import ro.sts.dgc.data.CertificateRepository
import ro.sts.dgc.model.VerificationResult
import timber.log.Timber
import java.security.Security
import javax.inject.Inject

/**
 * Encodes/decodes input as a Sign1Message according to COSE specification (RFC8152)
 */
open class DefaultCoseService @Inject constructor(
    private val certificateRepository: CertificateRepository,
) : CoseService {

    init {
        Security.addProvider(BouncyCastleProvider()) // for SHA256withRSA/PSS
    }

    override fun encode(input: ByteArray) = throw NotImplementedError()

    override fun decode(input: ByteArray, verificationResult: VerificationResult): ByteArray {
        verificationResult.coseVerified = false
        return try {
            (Sign1Message.DecodeFromBytes(input, MessageTag.Sign1) as Sign1Message)
                .also {
                    try {
                        val kid = it.findAttribute(HeaderKeys.KID)?.GetByteString() ?: throw IllegalArgumentException("kid")
                        verificationResult.kid = kid.asBase64()
                        certificateRepository.getCertificatesBy(kid.asBase64()).forEach { trustedCert ->
                            verificationResult.certificateValidFrom = trustedCert.validFrom
                            verificationResult.certificateValidUntil = trustedCert.validUntil
                            verificationResult.certificateValidContent = trustedCert.validContentTypes
                            if (it.validate(trustedCert.buildOneKey())) {
                                verificationResult.coseVerified = true
                                return it.GetContent()
                            }
                        }
                    } catch (e: Throwable) {
                        Timber.e(e, "Verification failed: COSE not decoded/validated")
                        it.GetContent()
                    }
                }.GetContent()
        } catch (e: Throwable) {
            Timber.e(e, "Verification failed: COSE not decoded")
            input
        }
    }

}