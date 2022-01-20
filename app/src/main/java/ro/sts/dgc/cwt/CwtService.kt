package ro.sts.dgc.cwt

import ro.sts.dgc.model.VerificationResult

/**
 * Encodes/decodes input as a CWT structure, ready to sign with COSE
 */
interface CwtService {

    fun encode(input: ByteArray): ByteArray

    fun decode(input: ByteArray, verificationResult: VerificationResult): ByteArray

}