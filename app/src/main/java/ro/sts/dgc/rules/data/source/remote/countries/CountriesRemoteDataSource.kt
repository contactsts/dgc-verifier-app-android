package ro.sts.dgc.rules.data.source.remote.countries

interface CountriesRemoteDataSource {
    suspend fun getCountries(countriesUrl: String): List<String>
}