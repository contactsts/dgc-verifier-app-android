package ro.sts.dgc.ui.model

import java.time.ZonedDateTime

data class RuleModel(
    val identifier: String,
    val type: String,
    val version: String,
    val schemaVersion: String,
    val engine: String,
    val engineVersion: String,
    val ruleCertificateType: String,

    val description: String,
    val validFrom: ZonedDateTime,
    val validTo: ZonedDateTime,
    val region: String?
)