package ro.sts.dgc.rules.data.source.local.rules

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "national_rule_identifiers")
data class NationalRuleIdentifierLocal(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val identifier: String,
    val version: String,
    val country: String,
    val hash: String
)