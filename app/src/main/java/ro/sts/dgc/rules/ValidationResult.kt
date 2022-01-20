package ro.sts.dgc.rules

import ro.sts.dgc.rules.data.Rule

enum class Result {
    PASSED, FAIL, OPEN
}

class ValidationResult(
    val rule: Rule,
    val result: Result,
    val current: String,
    val validationErrors: List<Exception>?,
)