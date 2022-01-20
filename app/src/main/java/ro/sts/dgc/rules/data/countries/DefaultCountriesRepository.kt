package ro.sts.dgc.rules.data.countries

import kotlinx.coroutines.flow.Flow
import ro.sts.dgc.rules.data.source.local.countries.CountriesLocalDataSource
import ro.sts.dgc.rules.data.source.remote.countries.CountriesRemoteDataSource
import java.util.*

class DefaultCountriesRepository(
    private val remoteDataSource: CountriesRemoteDataSource,
    private val localDataSource: CountriesLocalDataSource
) : CountriesRepository {

    override suspend fun preLoadCountries(countriesUrl: String) {
        remoteDataSource.getCountries(countriesUrl)
            .map { it.lowercase(Locale.ROOT) }
            .apply { localDataSource.updateCountries(this) }
    }

    override fun getCountries(): Flow<List<String>> {
        return localDataSource.getCountries()
    }
}