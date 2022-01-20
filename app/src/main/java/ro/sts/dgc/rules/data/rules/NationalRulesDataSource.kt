package ro.sts.dgc.rules.data.rules

import ro.sts.dgc.rules.data.Rule
import ro.sts.dgc.rules.data.RuleCertificateType
import ro.sts.dgc.rules.data.Type
import java.time.ZonedDateTime

interface NationalRulesDataSource {
    fun getRulesBy(
        countryIsoCode: String,
        validationClock: ZonedDateTime,
        type: Type,
        ruleCertificateType: RuleCertificateType
    ): List<Rule>

    fun getRulesBy(
        countryIsoCode: String
    ): List<Rule>
}