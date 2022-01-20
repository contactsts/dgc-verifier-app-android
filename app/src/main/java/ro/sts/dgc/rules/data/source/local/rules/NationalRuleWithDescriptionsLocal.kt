package ro.sts.dgc.rules.data.source.local.rules

import androidx.room.Embedded
import androidx.room.Relation

data class NationalRuleWithDescriptionsLocal(
    @Embedded val rule: NationalRuleLocal,
    @Relation(
        parentColumn = "ruleId",
        entityColumn = "ruleContainerId"
    )
    val descriptions: List<NationalDescriptionLocal>
)