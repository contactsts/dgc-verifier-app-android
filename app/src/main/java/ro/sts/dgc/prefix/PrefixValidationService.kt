package ro.sts.dgc.prefix

import ro.sts.dgc.model.VerificationResult

/**
 * Appends/drops a Context identifier prefix from input
 */
interface PrefixValidationService {

    fun encode(input: String): String

    fun decode(input: String, verificationResult: VerificationResult): String
}