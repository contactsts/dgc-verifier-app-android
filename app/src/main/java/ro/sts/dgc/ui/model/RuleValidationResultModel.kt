package ro.sts.dgc.ui.model

import ro.sts.dgc.rules.Result

data class RuleValidationResultModel(
    val description: String,
    val result: Result,
    val current: String,
    val countryIsoCode: String
)