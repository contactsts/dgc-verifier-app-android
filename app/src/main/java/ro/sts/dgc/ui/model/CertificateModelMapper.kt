package ro.sts.dgc.ui.model

import ro.sts.dgc.model.*

fun GreenCertificate.toCertificateModel(): CertificateModel {
    return CertificateModel(
        schemaVersion,
        subject.toPersonModel(),
        dateOfBirthString,
        dateOfBirth,
        vaccinations?.map { it.toVaccinationModel() },
        tests?.map { it.toTestModel() },
        recoveryStatements?.map { it.toRecoveryModel() }
    )
}

fun RecoveryStatement.toRecoveryModel(): RecoveryModel {
    return RecoveryModel(
        target,
        dateOfFirstPositiveTestResult,
        country,
        certificateIssuer,
        certificateValidFrom,
        certificateValidUntil,
        certificateIdentifier
    )
}

fun Test.toTestModel(): TestModel {
    return TestModel(
        target,
        type,
        nameNaa,
        nameRat,
        dateTimeSample,
        resultPositive,
        testFacility,
        country,
        certificateIssuer,
        certificateIdentifier
    )
}

fun Vaccination.toVaccinationModel(): VaccinationModel {
    return VaccinationModel(
        target,
        vaccine,
        medicinalProduct,
        authorizationHolder,
        doseNumber,
        doseTotalNumber,
        date,
        country,
        certificateIssuer,
        certificateIdentifier
    )
}

fun Person.toPersonModel(): PersonModel {
    return PersonModel(
        familyName,
        familyNameTransliterated,
        givenName,
        givenNameTransliterated
    )
}