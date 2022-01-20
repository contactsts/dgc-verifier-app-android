package ro.sts.dgc.rules.data.source.remote.countries

import retrofit2.Response

class DefaultCountriesRemoteDataSource(private val countriesApiService: CountriesApiService) : CountriesRemoteDataSource {
    override suspend fun getCountries(countriesUrl: String): List<String> {
        val countriesResponse: Response<List<String>> = countriesApiService.getCountries(countriesUrl)
        return countriesResponse.body() ?: listOf()
    }
}