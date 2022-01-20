package ro.sts.dgc.rules.data.source.local.valuesets

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fasterxml.jackson.databind.JsonNode
import java.time.LocalDate

@Entity(tableName = "valuesets")
class ValueSetLocal(
    @PrimaryKey
    val valueSetId: String,
    val valueSetDate: LocalDate,
    val valueSetValues: JsonNode
)