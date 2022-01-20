package ro.sts.dgc.rules.data.source.local.rules

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fasterxml.jackson.databind.JsonNode
import ro.sts.dgc.rules.data.RuleCertificateType
import ro.sts.dgc.rules.data.Type
import java.time.ZonedDateTime

@Entity(tableName = "national_rules")
data class NationalRuleLocal(
    @PrimaryKey(autoGenerate = true)
    val ruleId: Long = 0,
    val identifier: String,
    val type: Type,
    val version: String,
    val schemaVersion: String,
    val engine: String,
    val engineVersion: String,
    val ruleCertificateType: RuleCertificateType,
    val validFrom: ZonedDateTime,
    val validTo: ZonedDateTime,
    val affectedString: List<String>,
    val logic: JsonNode,
    val countryCode: String,
    val region: String?,
)