package ro.sts.dgc.rules.data.countries

interface CountriesRepository : CountriesDataSource {
    suspend fun preLoadCountries(countriesUrl: String)
}

val COUNTRIES_MAP = mapOf("el" to "gr")