package ro.sts.dgc.prefix

import ro.sts.dgc.model.Error
import ro.sts.dgc.model.VerificationResult

/**
 * Appends/drops a Context identifier prefix from input, e.g. "HC1:"
 */
open class DefaultPrefixValidationService(private val prefix: String = "HC1:") : PrefixValidationService {

    override fun encode(input: String): String {
        return "$prefix$input"
    }

    override fun decode(input: String, verificationResult: VerificationResult): String = when {
        input.startsWith(prefix) -> input.drop(prefix.length).also { verificationResult.contextIdentifier = prefix }
        else -> input.also {
            verificationResult.contextIdentifier = null
            verificationResult.error = Error.INVALID_SCHEME_PREFIX
        }
    }

}