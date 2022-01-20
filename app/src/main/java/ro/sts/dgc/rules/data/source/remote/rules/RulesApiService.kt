package ro.sts.dgc.rules.data.source.remote.rules

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface RulesApiService {

    @GET
    suspend fun getRuleIdentifiers(@Url rulesUrl: String): Response<List<RuleIdentifierRemote>>

    @GET
    suspend fun getRules(@Url url: String): Response<List<RuleRemote>>

    @GET
    suspend fun getRule(@Url url: String): Response<RuleRemote>

}