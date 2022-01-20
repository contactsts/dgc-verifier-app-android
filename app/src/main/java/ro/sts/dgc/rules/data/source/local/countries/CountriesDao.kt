package ro.sts.dgc.rules.data.source.local.countries

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CountriesDao {
    @Query("SELECT * from countries")
    fun getAll(): Flow<List<CountryLocal>>

    @Insert
    fun insertAll(vararg countriesLocal: CountryLocal)

    @Query("DELETE FROM countries")
    fun deleteAll()
}