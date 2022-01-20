package ro.sts.dgc.rules.data.source.local.rules

import ro.sts.dgc.rules.data.Rule
import ro.sts.dgc.rules.data.RuleIdentifier
import ro.sts.dgc.rules.data.rules.NationalRulesDataSource

interface NationalRulesLocalDataSource : NationalRulesDataSource {
    fun addRules(ruleIdentifiers: Collection<RuleIdentifier>, rules: Collection<Rule>)

    fun removeRulesBy(identifiers: Collection<String>)

    fun getRuleIdentifiers(): List<RuleIdentifier>
}