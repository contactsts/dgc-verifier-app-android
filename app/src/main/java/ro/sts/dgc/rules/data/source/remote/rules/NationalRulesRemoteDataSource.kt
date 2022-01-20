package ro.sts.dgc.rules.data.source.remote.rules

import ro.sts.dgc.rules.data.Rule
import ro.sts.dgc.rules.data.RuleIdentifier

interface NationalRulesRemoteDataSource {
    suspend fun getRuleIdentifiers(rulesUrl: String): List<RuleIdentifier>

    suspend fun getRules(rulesUrl: String): List<Rule>

    suspend fun getRule(ruleUrl: String): Rule?
}