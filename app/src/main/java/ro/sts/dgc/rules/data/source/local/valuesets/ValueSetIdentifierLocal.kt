package ro.sts.dgc.rules.data.source.local.valuesets

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "value_set_identifier")
class ValueSetIdentifierLocal(
    @PrimaryKey
    val valueSetIdentifierId: String,
    val valueSetHash: String
)