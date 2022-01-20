package ro.sts.dgc.rules.data.countries

import kotlinx.coroutines.flow.Flow

interface CountriesDataSource {
    /**
     * Provides list of countries ISO codes.
     */
    fun getCountries(): Flow<List<String>>
}