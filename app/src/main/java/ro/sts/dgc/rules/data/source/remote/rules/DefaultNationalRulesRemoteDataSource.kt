package ro.sts.dgc.rules.data.source.remote.rules

import retrofit2.Response
import ro.sts.dgc.rules.data.Rule
import ro.sts.dgc.rules.data.RuleIdentifier

class DefaultNationalRulesRemoteDataSource(private val nationalRulesApiService: NationalRulesApiService) : NationalRulesRemoteDataSource {

    override suspend fun getRuleIdentifiers(rulesUrl: String): List<RuleIdentifier> {
        val rulesResponse: Response<List<RuleIdentifierRemote>> = nationalRulesApiService.getNationalRuleIdentifiers(rulesUrl)
        return rulesResponse.body()?.map { it.toRuleIdentifier() } ?: listOf()
    }

    override suspend fun getRules(rulesUrl: String): List<Rule> {
        val rulesResponse: Response<List<RuleRemote>> = nationalRulesApiService.getNationalRules(rulesUrl)
        return rulesResponse.body()?.map { it.toRule() } ?: listOf()
    }

    override suspend fun getRule(ruleUrl: String): Rule? {
        val ruleResponse: Response<RuleRemote> = nationalRulesApiService.getNationalRule(ruleUrl)
        return ruleResponse.body()?.toRule()
    }
}