package ro.sts.dgc.rules.data.source.local.countries

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*

class DefaultCountriesLocalDataSource(private val countriesDao: CountriesDao) :
    CountriesLocalDataSource {
    override suspend fun updateCountries(countriesIsoCodes: List<String>) {
        countriesDao.apply {
            deleteAll()
            insertAll(*countriesIsoCodes.map { it.toCountryLocal() }.toTypedArray())
        }
    }

    override fun getCountries(): Flow<List<String>> =
        countriesDao.getAll().map { it.map { it.toCountry() } }
}

fun String.toCountryLocal(): CountryLocal = CountryLocal(isoCode = this.lowercase(Locale.ROOT))

fun CountryLocal.toCountry(): String = this.isoCode