package ro.sts.dgc.rules.data.source.local.rules

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "descriptions",
    foreignKeys = [ForeignKey(
        entity = RuleLocal::class,
        parentColumns = arrayOf("ruleId"),
        childColumns = arrayOf("ruleContainerId"),
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(
        value = ["ruleContainerId"]
    )]
)
data class DescriptionLocal(
    @PrimaryKey(autoGenerate = true)
    val descriptionId: Long = 0,
    val ruleContainerId: Long = 0,
    val lang: String,
    val desc: String
)