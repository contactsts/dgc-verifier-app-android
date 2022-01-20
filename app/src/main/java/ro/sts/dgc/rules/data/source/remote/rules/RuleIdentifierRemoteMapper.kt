package ro.sts.dgc.rules.data.source.remote.rules

import ro.sts.dgc.rules.data.RuleIdentifier
import java.util.*

fun RuleIdentifierRemote.toRuleIdentifier(): RuleIdentifier = RuleIdentifier(
    identifier = this.identifier,
    version = this.version,
    country = this.country.lowercase(Locale.ROOT),
    hash = this.hash
)