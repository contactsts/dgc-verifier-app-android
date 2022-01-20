package ro.sts.dgc.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ro.sts.dgc.schema.payload.v1.RecoveryEntry
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset

@Serializable
data class RecoveryStatement(
    @SerialName("tg")
    val target: ValueSetEntryAdapter,

    @SerialName("fr")
    @Serializable(with = LocalDateSerializer::class)
    val dateOfFirstPositiveTestResult: LocalDate,

    @SerialName("co")
    val country: ValueSetEntryAdapter,

    @SerialName("is")
    val certificateIssuer: String,

    @SerialName("df")
    @Serializable(with = LocalDateSerializer::class)
    val certificateValidFrom: LocalDate,

    @SerialName("du")
    @Serializable(with = LocalDateSerializer::class)
    val certificateValidUntil: LocalDate,

    @SerialName("ci")
    val certificateIdentifier: String,
) {
    fun isCertificateNotValidAnymore(): Boolean =
        certificateValidUntil.isBefore(LocalDate.now())

    fun isCertificateNotValidSoFar(): Boolean =
        certificateValidFrom.isAfter(LocalDate.now())

    fun toEuSchema() = RecoveryEntry().apply {
        tg = target.key
        fr = dateOfFirstPositiveTestResult
        co = country.key
        `is` = certificateIssuer
        df = certificateValidFrom
        du = certificateValidUntil
        ci = certificateIdentifier
    }

    companion object {
        private val UTC_ZONE_ID: ZoneId = ZoneId.ofOffset("", ZoneOffset.UTC).normalized()

        @JvmStatic
        fun fromEuSchema(it: RecoveryEntry, valueSets: ValueSetHolder) = RecoveryStatement(
            target = valueSets.find(ValueSetHolder.DISEASE_AGENT_TARGETED_ID, it.tg),
            dateOfFirstPositiveTestResult = it.fr,
            country = valueSets.find(ValueSetHolder.COUNTRY_CODES_ID, it.co),
            certificateIssuer = it.`is`,
            certificateValidFrom = it.df,
            certificateValidUntil = it.du,
            certificateIdentifier = it.ci
        )
    }
}