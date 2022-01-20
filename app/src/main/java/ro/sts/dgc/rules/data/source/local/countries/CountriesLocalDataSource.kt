package ro.sts.dgc.rules.data.source.local.countries

import ro.sts.dgc.rules.data.countries.CountriesDataSource

interface CountriesLocalDataSource : CountriesDataSource {
    /**
     * Replaces local countries list.
     */
    suspend fun updateCountries(countriesIsoCodes: List<String>)
}