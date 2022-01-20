package ro.sts.dgc.rules.data.source.remote.rules

import retrofit2.Response
import ro.sts.dgc.rules.data.Rule
import ro.sts.dgc.rules.data.RuleIdentifier

class DefaultRulesRemoteDataSource(private val rulesApiService: RulesApiService) : RulesRemoteDataSource {

    override suspend fun getRuleIdentifiers(rulesUrl: String): List<RuleIdentifier> {
        val rulesResponse: Response<List<RuleIdentifierRemote>> = rulesApiService.getRuleIdentifiers(rulesUrl)
        return rulesResponse.body()?.map { it.toRuleIdentifier() } ?: listOf()
    }

    override suspend fun getRules(rulesUrl: String): List<Rule> {
        val rulesResponse: Response<List<RuleRemote>> = rulesApiService.getRules(rulesUrl)
        return rulesResponse.body()?.map { it.toRule() } ?: listOf()
    }

    override suspend fun getRule(ruleUrl: String): Rule? {
        val ruleResponse: Response<RuleRemote> = rulesApiService.getRule(ruleUrl)
        return ruleResponse.body()?.toRule()
    }
}