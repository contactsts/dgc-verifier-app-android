package ro.sts.dgc.rules.data.source.local.rules

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rule_identifiers")
data class RuleIdentifierLocal(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val identifier: String,
    val version: String,
    val country: String,
    val hash: String
)