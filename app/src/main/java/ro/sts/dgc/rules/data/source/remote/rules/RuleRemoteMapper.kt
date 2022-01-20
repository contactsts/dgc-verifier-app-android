package ro.sts.dgc.rules.data.source.remote.rules

import ro.sts.dgc.rules.data.Description
import ro.sts.dgc.rules.data.Rule
import ro.sts.dgc.rules.data.RuleCertificateType
import ro.sts.dgc.rules.data.Type
import java.util.*

fun RuleRemote.toRule(): Rule = Rule(
    identifier = this.identifier,
    type = Type.valueOf(this.type.uppercase(Locale.ROOT)),
    version = this.version,
    schemaVersion = this.schemaVersion,
    engine = this.engine,
    engineVersion = this.engineVersion,
    ruleCertificateType = RuleCertificateType.valueOf(this.certificateType.uppercase(Locale.ROOT)),
    descriptions = this.descriptions.toDescriptions(),
    validFrom = this.validFrom,
    validTo = this.validTo,
    affectedString = this.affectedString,
    logic = this.logic,
    countryCode = this.countryCode.lowercase(Locale.ROOT),
    region = this.region
)

fun List<RuleRemote>.toRules(): List<Rule> {
    val rules = mutableListOf<Rule>()
    forEach {
        rules.add(it.toRule())
    }
    return rules
}

fun DescriptionRemote.toDescriptions(): Description = Description(
    lang = this.lang,
    desc = this.desc
)

fun List<DescriptionRemote>.toDescriptions(): Map<String, String> {
    val descriptions = mutableMapOf<String, String>()
    forEach {
        descriptions[it.lang.lowercase(Locale.ROOT)] = it.desc
    }
    return descriptions
}