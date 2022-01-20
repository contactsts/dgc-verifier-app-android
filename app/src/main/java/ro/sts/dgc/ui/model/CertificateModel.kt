package ro.sts.dgc.ui.model

import ro.sts.dgc.model.ValueSetEntryAdapter
import java.time.Instant
import java.time.LocalDate
import java.util.*

data class CertificateModel(
    val schemaVersion: String,
    val person: PersonModel,
    val dateOfBirthString: String?,
    val dateOfBirth: LocalDate?,
    val vaccinations: List<VaccinationModel>?,
    val tests: List<TestModel>?,
    val recoveryStatements: List<RecoveryModel>?
) {
    fun getIssuingCountry(): String = try {
        when {
            vaccinations?.isNotEmpty() == true -> vaccinations.first().country.key
            tests?.isNotEmpty() == true -> tests.first().country.key
            recoveryStatements?.isNotEmpty() == true -> recoveryStatements.first().country.key
            else -> ""
        }
    } catch (ex: Exception) {
        ""
    }.lowercase(Locale.ROOT)
}

data class PersonModel(
    val familyName: String?,
    val familyNameTransliterated: String?,
    val givenName: String?,
    val givenNameTransliterated: String?
)

data class VaccinationModel(
    override val target: ValueSetEntryAdapter,
    val vaccine: ValueSetEntryAdapter,
    val medicinalProduct: ValueSetEntryAdapter,
    val authorizationHolder: ValueSetEntryAdapter,
    val doseNumber: Int,
    val doseTotalNumber: Int,
    val date: LocalDate,
    val country: ValueSetEntryAdapter,
    val certificateIssuer: String,
    val certificateIdentifier: String
) : CertificateData

data class TestModel(
    override val target: ValueSetEntryAdapter,
    val type: ValueSetEntryAdapter,
    val nameNaa: String?,
    val nameRat: ValueSetEntryAdapter?,
    val dateTimeSample: Instant,
    val resultPositive: ValueSetEntryAdapter,
    val testFacility: String?,
    val country: ValueSetEntryAdapter,
    val certificateIssuer: String,
    val certificateIdentifier: String
) : CertificateData {
    companion object {
        const val NOT_DETECTED = "260415000"
    }
}

data class RecoveryModel(
    override val target: ValueSetEntryAdapter,
    val dateOfFirstPositiveTestResult: LocalDate,
    val country: ValueSetEntryAdapter,
    val certificateIssuer: String,
    val certificateValidFrom: LocalDate,
    val certificateValidUntil: LocalDate,
    val certificateIdentifier: String
) : CertificateData

interface CertificateData {
    val target: ValueSetEntryAdapter
}