package ro.sts.dgc.compression

import ro.sts.dgc.model.Error
import ro.sts.dgc.model.VerificationResult
import java.util.zip.Deflater
import java.util.zip.DeflaterInputStream
import java.util.zip.InflaterInputStream

/**
 * Compresses/decompresses input with ZLIB, [level] specifies the compression level (0-9)
 */
open class DefaultCompressorService(private val level: Int = 9) : CompressorService {

    /**
     * Compresses input with ZLIB = deflating
     */
    override fun encode(input: ByteArray): ByteArray {
        return DeflaterInputStream(input.inputStream(), Deflater(level)).readBytes()
    }

    /**
     * Decompresses input with ZLIB = inflating.
     */
    override fun decode(input: ByteArray, verificationResult: VerificationResult): ByteArray {
        verificationResult.zlibDecoded = false
        return try {
            InflaterInputStream(input.inputStream()).readBytes().also {
                verificationResult.zlibDecoded = true
            }
        } catch (e: Throwable) {
            input.also {
                verificationResult.error = Error.DECOMPRESSION_FAILED
            }
        }
    }

}