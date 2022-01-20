package ro.sts.dgc.rules.domain

import ro.sts.dgc.rules.data.CertificateType
import ro.sts.dgc.rules.data.Rule
import java.time.ZonedDateTime

interface GetRulesUseCase {
    fun invoke(
        validationClock: ZonedDateTime,
        acceptanceCountryIsoCode: String,
        issuanceCountryIsoCode: String,
        certificateType: CertificateType,
        region: String? = null,
        useNationalRules: Boolean? = false
    ): List<Rule>

    fun invoke(
        acceptanceCountryIsoCode: String
    ): List<Rule>
}