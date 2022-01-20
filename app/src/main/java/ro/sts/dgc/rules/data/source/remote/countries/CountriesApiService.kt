package ro.sts.dgc.rules.data.source.remote.countries

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface CountriesApiService {

    @GET
    suspend fun getCountries(@Url url: String): Response<List<String>>
}