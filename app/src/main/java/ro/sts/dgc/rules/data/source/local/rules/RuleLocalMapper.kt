package ro.sts.dgc.rules.data.source.local.rules

import ro.sts.dgc.rules.UTC_ZONE_ID
import ro.sts.dgc.rules.data.Description
import ro.sts.dgc.rules.data.Rule
import java.util.*

fun Rule.toRuleWithDescriptionLocal(): RuleWithDescriptionsLocal =
    RuleWithDescriptionsLocal(this.toRuleLocal(), descriptions.toDescriptionsLocal())

fun Rule.toNationalRuleWithDescriptionLocal(): NationalRuleWithDescriptionsLocal =
    NationalRuleWithDescriptionsLocal(this.toNationalRuleLocal(), descriptions.toNationalDescriptionsLocal())

fun List<Rule>.toRulesWithDescriptionLocal(): List<RuleWithDescriptionsLocal> {
    val rulesWithDescriptionLocal = mutableListOf<RuleWithDescriptionsLocal>()
    forEach {
        rulesWithDescriptionLocal.add(it.toRuleWithDescriptionLocal())
    }
    return rulesWithDescriptionLocal
}

fun Rule.toRuleLocal(): RuleLocal = RuleLocal(
    identifier = this.identifier,
    type = this.type,
    version = this.version,
    schemaVersion = this.schemaVersion,
    engine = this.engine,
    engineVersion = this.engineVersion,
    ruleCertificateType = this.ruleCertificateType,
    validFrom = this.validFrom.withZoneSameInstant(UTC_ZONE_ID),
    validTo = this.validTo.withZoneSameInstant(UTC_ZONE_ID),
    affectedString = this.affectedString,
    logic = this.logic,
    countryCode = this.countryCode,
    region = this.region
)

fun Rule.toNationalRuleLocal(): NationalRuleLocal = NationalRuleLocal(
    identifier = this.identifier,
    type = this.type,
    version = this.version,
    schemaVersion = this.schemaVersion,
    engine = this.engine,
    engineVersion = this.engineVersion,
    ruleCertificateType = this.ruleCertificateType,
    validFrom = this.validFrom.withZoneSameInstant(UTC_ZONE_ID),
    validTo = this.validTo.withZoneSameInstant(UTC_ZONE_ID),
    affectedString = this.affectedString,
    logic = this.logic,
    countryCode = this.countryCode,
    region = this.region
)

fun Description.toDescriptionLocal(): DescriptionLocal =
    DescriptionLocal(lang = this.lang, desc = this.desc)

fun Map<String, String>.toDescriptionsLocal(): List<DescriptionLocal> {
    val descriptionsLocal = mutableListOf<DescriptionLocal>()
    forEach { descriptionsLocal.add(DescriptionLocal(lang = it.key, desc = it.value)) }
    return descriptionsLocal
}

fun Map<String, String>.toNationalDescriptionsLocal(): List<NationalDescriptionLocal> {
    val descriptionsLocal = mutableListOf<NationalDescriptionLocal>()
    forEach { descriptionsLocal.add(NationalDescriptionLocal(lang = it.key, desc = it.value)) }
    return descriptionsLocal
}

fun DescriptionLocal.toDescription(): Description = Description(lang = this.lang, desc = this.desc)

fun List<DescriptionLocal>.toDescriptions(): Map<String, String> {
    val descriptions = mutableMapOf<String, String>()
    forEach { descriptions[it.lang.lowercase(Locale.ROOT)] = it.desc }
    return descriptions
}

fun List<NationalDescriptionLocal>.toNationalDescriptions(): Map<String, String> {
    val descriptions = mutableMapOf<String, String>()
    forEach { descriptions[it.lang.lowercase(Locale.ROOT)] = it.desc }
    return descriptions
}

fun RuleWithDescriptionsLocal.toRule(): Rule = Rule(
    identifier = this.rule.identifier,
    type = this.rule.type,
    version = this.rule.version,
    schemaVersion = this.rule.schemaVersion,
    engine = this.rule.engine,
    engineVersion = this.rule.engineVersion,
    ruleCertificateType = this.rule.ruleCertificateType,
    validFrom = this.rule.validFrom.withZoneSameInstant(UTC_ZONE_ID),
    validTo = this.rule.validTo.withZoneSameInstant(UTC_ZONE_ID),
    affectedString = this.rule.affectedString,
    logic = this.rule.logic,
    countryCode = this.rule.countryCode,
    descriptions = this.descriptions.toDescriptions(),
    region = this.rule.region
)

fun NationalRuleWithDescriptionsLocal.toRule(): Rule = Rule(
    identifier = this.rule.identifier,
    type = this.rule.type,
    version = this.rule.version,
    schemaVersion = this.rule.schemaVersion,
    engine = this.rule.engine,
    engineVersion = this.rule.engineVersion,
    ruleCertificateType = this.rule.ruleCertificateType,
    validFrom = this.rule.validFrom.withZoneSameInstant(UTC_ZONE_ID),
    validTo = this.rule.validTo.withZoneSameInstant(UTC_ZONE_ID),
    affectedString = this.rule.affectedString,
    logic = this.rule.logic,
    countryCode = this.rule.countryCode,
    descriptions = this.descriptions.toNationalDescriptions(),
    region = this.rule.region
)

fun List<RuleWithDescriptionsLocal>.toRules(): List<Rule> {
    val rules = mutableListOf<Rule>()
    forEach {
        rules.add(it.toRule())
    }
    return rules
}

fun List<NationalRuleWithDescriptionsLocal>.toNationalRules(): List<Rule> {
    val rules = mutableListOf<Rule>()
    forEach {
        rules.add(it.toRule())
    }
    return rules
}