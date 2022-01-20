package ro.sts.dgc.encoding

import ro.sts.dgc.model.VerificationResult

interface Base45Service {

    fun decode(input: String, verificationResult: VerificationResult): ByteArray
}