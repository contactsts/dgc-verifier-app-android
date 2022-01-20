package ro.sts.dgc.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ro.sts.dgc.schema.payload.v1.Eudcc
import java.time.LocalDate

@Serializable
data class GreenCertificate(
    @SerialName("ver")
    val schemaVersion: String,

    @SerialName("nam")
    val subject: Person,

    @SerialName("dob")
    val dateOfBirthString: String,

    @SerialName("v")
    val vaccinations: List<Vaccination>? = null,

    @SerialName("r")
    val recoveryStatements: List<RecoveryStatement>? = null,

    @SerialName("t")
    val tests: List<Test>? = null,
) {
    /**
     * For [dateOfBirthString] ("dob"), month and day are optional in eu-dcc-schema 1.2.1, so we may not be able to get a valid [LocalDate] from it.
     */
    val dateOfBirth: LocalDate?
        get() = try {
            LocalDate.parse(dateOfBirthString)
        } catch (e: Throwable) {
            null
        }

    fun toEuSchema() = Eudcc().apply {
        ver = schemaVersion
        nam = subject.toEuSchema()
        dob = dateOfBirthString
        v = vaccinations?.map { it.toEuSchema() }?.toList()?.ifEmpty { null }
        r = recoveryStatements?.map { it.toEuSchema() }?.toList()?.ifEmpty { null }
        t = tests?.map { it.toEuSchema() }?.toList()?.ifEmpty { null }
    }

    companion object {

        @JvmStatic
        fun fromEuSchema(input: Eudcc, valueSets: ValueSetHolder): GreenCertificate? {
            if (input.nam == null || input.ver == null || input.dob == null) {
                return null
            }
            return try {
                GreenCertificate(
                    schemaVersion = input.ver,
                    subject = Person.fromEuSchema(input.nam),
                    dateOfBirthString = input.dob,
                    vaccinations = input.v?.let { it.map { v -> Vaccination.fromEuSchema(v, valueSets) } },
                    recoveryStatements = input.r?.let { it.map { r -> RecoveryStatement.fromEuSchema(r, valueSets) } },
                    tests = input.t?.let { it.map { t -> Test.fromEuSchema(t, valueSets) } },
                )
            } catch (e: Throwable) {
                null
            }
        }
    }
}