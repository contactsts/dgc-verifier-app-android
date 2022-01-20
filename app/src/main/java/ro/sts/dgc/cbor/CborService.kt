package ro.sts.dgc.cbor

import ro.sts.dgc.model.VerificationResult
import ro.sts.dgc.schema.payload.v1.Eudcc

/**
 * Encodes/decodes input as a CBOR structure
 */
interface CborService {

    fun encode(input: Eudcc): ByteArray

    fun decode(input: ByteArray, verificationResult: VerificationResult): Eudcc

}