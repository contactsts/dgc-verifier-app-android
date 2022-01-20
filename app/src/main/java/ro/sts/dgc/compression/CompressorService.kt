package ro.sts.dgc.compression

import ro.sts.dgc.model.VerificationResult

/**
 * Compresses/decompresses input
 */
interface CompressorService {

    fun encode(input: ByteArray): ByteArray

    fun decode(input: ByteArray, verificationResult: VerificationResult): ByteArray

}