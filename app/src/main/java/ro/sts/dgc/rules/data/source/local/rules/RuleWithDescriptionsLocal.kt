package ro.sts.dgc.rules.data.source.local.rules

import androidx.room.Embedded
import androidx.room.Relation

data class RuleWithDescriptionsLocal(
    @Embedded val rule: RuleLocal,
    @Relation(
        parentColumn = "ruleId",
        entityColumn = "ruleContainerId"
    )
    val descriptions: List<DescriptionLocal>
)