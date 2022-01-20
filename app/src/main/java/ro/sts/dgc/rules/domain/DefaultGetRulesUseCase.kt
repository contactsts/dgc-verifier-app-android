package ro.sts.dgc.rules.domain

import ro.sts.dgc.rules.data.CertificateType
import ro.sts.dgc.rules.data.Rule
import ro.sts.dgc.rules.data.Type
import ro.sts.dgc.rules.data.rules.NationalRulesRepository
import ro.sts.dgc.rules.data.rules.RulesRepository
import java.time.ZonedDateTime

class DefaultGetRulesUseCase(
    private val rulesRepository: RulesRepository,
    private val nationalRulesRepository: NationalRulesRepository
) : GetRulesUseCase {
    override fun invoke(
        validationClock: ZonedDateTime,
        acceptanceCountryIsoCode: String,
        issuanceCountryIsoCode: String,
        certificateType: CertificateType,
        region: String?,
        useNationalRules: Boolean?
    ): List<Rule> {
        val acceptanceRules = mutableMapOf<String, Rule>()
        val selectedRegion: String = region?.trim() ?: ""
        val invalidationRules = mutableMapOf<String, Rule>()

        if (useNationalRules == true) {
            nationalRulesRepository.getRulesBy(acceptanceCountryIsoCode, validationClock, Type.ACCEPTANCE, certificateType.toRuleCertificateType()).forEach {
                val ruleRegion: String = it.region?.trim() ?: ""
                if ((selectedRegion.equals(ruleRegion, ignoreCase = true) || acceptanceCountryIsoCode.equals(ruleRegion, ignoreCase = true)) &&
                    (acceptanceRules[it.identifier]?.version?.toVersion() ?: -1 < it.version.toVersion() ?: 0)
                ) {
                    acceptanceRules[it.identifier] = it
                }
            }
            if (issuanceCountryIsoCode.isNotBlank()) {
                nationalRulesRepository.getRulesBy(issuanceCountryIsoCode, validationClock, Type.INVALIDATION, certificateType.toRuleCertificateType()).forEach {
                    if (invalidationRules[it.identifier]?.version?.toVersion() ?: -1 < it.version.toVersion() ?: 0) {
                        invalidationRules[it.identifier] = it
                    }
                }
            }
        } else {
            rulesRepository.getRulesBy(acceptanceCountryIsoCode, validationClock, Type.ACCEPTANCE, certificateType.toRuleCertificateType()).forEach {
                val ruleRegion: String = it.region?.trim() ?: ""
                if ((selectedRegion.equals(ruleRegion, ignoreCase = true) || acceptanceCountryIsoCode.equals(ruleRegion, ignoreCase = true)) &&
                    (acceptanceRules[it.identifier]?.version?.toVersion() ?: -1 < it.version.toVersion() ?: 0)
                ) {
                    acceptanceRules[it.identifier] = it
                }
            }
            if (issuanceCountryIsoCode.isNotBlank()) {
                rulesRepository.getRulesBy(issuanceCountryIsoCode, validationClock, Type.INVALIDATION, certificateType.toRuleCertificateType()).forEach {
                    if (invalidationRules[it.identifier]?.version?.toVersion() ?: -1 < it.version.toVersion() ?: 0) {
                        invalidationRules[it.identifier] = it
                    }
                }
            }
        }
        return acceptanceRules.values + invalidationRules.values
    }

    override fun invoke(
        acceptanceCountryIsoCode: String
    ): List<Rule> {
        return rulesRepository.getRulesBy(acceptanceCountryIsoCode)
    }

    private fun String.toVersion(): Int? = try {
        val versionParts = this.split('.')
        var version = 0
        var multiplier = 1
        versionParts.reversed().forEach {
            version += multiplier * it.toInt()
            multiplier *= 100
        }
        version
    } catch (error: Throwable) {
        null
    }
}