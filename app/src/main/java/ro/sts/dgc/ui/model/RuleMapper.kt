package ro.sts.dgc.ui.model

import ro.sts.dgc.rules.data.Rule
import java.util.*

fun Rule.toRuleModel(): RuleModel {
    return RuleModel(
        this.identifier,
        this.type.toString(),
        this.version,
        this.schemaVersion,
        this.engine,
        this.engineVersion,
        this.ruleCertificateType.toString(),
        this.getDescriptionFor(Locale.getDefault().language),
        this.validFrom,
        this.validTo,
        this.region
    )
}