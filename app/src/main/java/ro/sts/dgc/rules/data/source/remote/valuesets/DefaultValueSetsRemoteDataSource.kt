package ro.sts.dgc.rules.data.source.remote.valuesets

import retrofit2.Response
import timber.log.Timber

class DefaultValueSetsRemoteDataSource(private val apiService: ValueSetsApiService) : ValueSetsRemoteDataSource {
    override suspend fun getValueSetsIdentifiers(url: String): List<ValueSetIdentifierRemote> {
        val response: Response<List<ValueSetIdentifierRemote>> = apiService.getValueSetsIdentifiers(url)
        return response.body() ?: listOf()
    }

    override suspend fun getValueSet(url: String): ValueSetRemote? {
        Timber.i("getValueSet $url")
        val ruleResponse: Response<ValueSetRemote> = apiService.getValueSet(url)
        return ruleResponse.body()
    }
}