package ro.sts.dgc.rules.data.source.local.rules

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "national_descriptions",
    foreignKeys = [ForeignKey(
        entity = NationalRuleLocal::class,
        parentColumns = arrayOf("ruleId"),
        childColumns = arrayOf("ruleContainerId"),
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(
        value = ["ruleContainerId"]
    )]
)
data class NationalDescriptionLocal(
    @PrimaryKey(autoGenerate = true)
    val descriptionId: Long = 0,
    val ruleContainerId: Long = 0,
    val lang: String,
    val desc: String
)