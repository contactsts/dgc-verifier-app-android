package ro.sts.dgc.encoding

import ro.sts.dgc.model.Error
import ro.sts.dgc.model.VerificationResult

/**
 * Encodes/decodes input in/from Base45
 */
open class DefaultBase45Service : Base45Service {

    override fun decode(input: String, verificationResult: VerificationResult): ByteArray {
        verificationResult.base45Decoded = false
        return try {
            Base45.getDecoder().decode(input).also {
                verificationResult.base45Decoded = true
            }
        } catch (e: Throwable) {
            input.toByteArray().also {
                verificationResult.error = Error.BASE_45_DECODING_FAILED
            }
        }
    }

}