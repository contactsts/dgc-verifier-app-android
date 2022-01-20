package ro.sts.dgc.rules.data.source.remote.valuesets

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface ValueSetsApiService {

    @GET
    suspend fun getValueSetsIdentifiers(@Url url: String): Response<List<ValueSetIdentifierRemote>>

    @GET
    suspend fun getValueSet(@Url url: String): Response<ValueSetRemote>
}