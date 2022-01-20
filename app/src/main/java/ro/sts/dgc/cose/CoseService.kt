package ro.sts.dgc.cose

import ro.sts.dgc.model.VerificationResult

/**
 * Encodes/decodes input as a Sign1Message according to COSE specification (RFC8152)
 */
interface CoseService {

    fun encode(input: ByteArray): ByteArray

    fun decode(input: ByteArray, verificationResult: VerificationResult): ByteArray

}