package ro.sts.dgc.ui.model

import ro.sts.dgc.rules.ValidationResult
import java.util.*

fun ValidationResult.toRuleValidationResultModel(): RuleValidationResultModel {
    return RuleValidationResultModel(
        this.rule.getDescriptionFor(Locale.getDefault().language),
        this.result,
        this.current,
        this.rule.countryCode
    )
}