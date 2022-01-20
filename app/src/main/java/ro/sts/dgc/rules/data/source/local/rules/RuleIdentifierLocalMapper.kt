package ro.sts.dgc.rules.data.source.local.rules

import ro.sts.dgc.rules.data.RuleIdentifier

fun RuleIdentifier.toRuleIdentifierLocal() = RuleIdentifierLocal(
    identifier = this.identifier,
    version = this.version,
    country = this.country,
    hash = this.hash
)

fun RuleIdentifier.toNationalRuleIdentifierLocal() = NationalRuleIdentifierLocal(
    identifier = this.identifier,
    version = this.version,
    country = this.country,
    hash = this.hash
)

fun RuleIdentifierLocal.toRuleIdentifier() = RuleIdentifier(
    identifier = this.identifier,
    version = this.version,
    country = this.country,
    hash = this.hash
)

fun NationalRuleIdentifierLocal.toRuleIdentifier() = RuleIdentifier(
    identifier = this.identifier,
    version = this.version,
    country = this.country,
    hash = this.hash
)