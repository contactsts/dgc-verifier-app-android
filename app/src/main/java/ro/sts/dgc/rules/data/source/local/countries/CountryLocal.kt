package ro.sts.dgc.rules.data.source.local.countries

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "countries")
data class CountryLocal(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val isoCode: String,
)