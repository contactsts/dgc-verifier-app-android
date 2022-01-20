package ro.sts.dgc.rules.data

import com.fasterxml.jackson.databind.JsonNode
import java.time.ZonedDateTime
import java.util.*

data class Rule(
    val identifier: String,
    val type: Type,
    val version: String,
    val schemaVersion: String,
    val engine: String,
    val engineVersion: String,
    val ruleCertificateType: RuleCertificateType,
    val descriptions: Map<String, String>,
    val validFrom: ZonedDateTime,
    val validTo: ZonedDateTime,
    val affectedString: List<String>,
    val logic: JsonNode,
    val countryCode: String,
    val region: String?
) {
    fun getDescriptionFor(languageCode: String): String {
        val description = descriptions[languageCode.lowercase(Locale.ROOT)]
        return if (description?.isNotBlank() == true) description else descriptions[Locale.ENGLISH.language] ?: ""
    }
}