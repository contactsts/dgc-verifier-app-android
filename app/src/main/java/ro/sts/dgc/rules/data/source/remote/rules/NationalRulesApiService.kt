package ro.sts.dgc.rules.data.source.remote.rules

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface NationalRulesApiService {

    @GET
    suspend fun getNationalRuleIdentifiers(@Url rulesUrl: String): Response<List<RuleIdentifierRemote>>

    @GET
    suspend fun getNationalRules(@Url url: String): Response<List<RuleRemote>>

    @GET
    suspend fun getNationalRule(@Url url: String): Response<RuleRemote>

}