package ro.sts.dgc.rules.data.rules

import ro.sts.dgc.rules.data.Rule
import ro.sts.dgc.rules.data.RuleCertificateType
import ro.sts.dgc.rules.data.RuleIdentifier
import ro.sts.dgc.rules.data.Type
import ro.sts.dgc.rules.data.source.local.rules.NationalRulesLocalDataSource
import ro.sts.dgc.rules.data.source.remote.rules.NationalRulesRemoteDataSource
import java.time.ZonedDateTime

class DefaultNationalRulesRepository(
    private val remoteDataSource: NationalRulesRemoteDataSource,
    private val localDataSource: NationalRulesLocalDataSource
) : NationalRulesRepository {
    override suspend fun loadRules(rulesUrl: String) {
        val upToDateRuleIdentifiers = remoteDataSource.getRuleIdentifiers(rulesUrl).toMutableSet()
        val rulesToBeRemovedIdentifiers = localDataSource.getRuleIdentifiers()
            .filter { !upToDateRuleIdentifiers.remove(it) }
            .map { it.identifier }

        localDataSource.removeRulesBy(rulesToBeRemovedIdentifiers)

        val ruleIdentifiersToBeSaved = mutableListOf<RuleIdentifier>()
        val rulesToBeSaved = mutableListOf<Rule>()
        upToDateRuleIdentifiers.forEach { ruleIdentifier ->
            try {
                remoteDataSource.getRule("$rulesUrl/${ruleIdentifier.hash}")
                    ?.let { rule ->
                        ruleIdentifiersToBeSaved.add(ruleIdentifier)
                        rulesToBeSaved.add(rule)
                    }
            } catch (error: Throwable) {
            }
        }

        if (ruleIdentifiersToBeSaved.isNotEmpty()) {
            localDataSource.addRules(ruleIdentifiersToBeSaved, rulesToBeSaved)
        }
    }

    override fun getRulesBy(
        countryIsoCode: String,
        validationClock: ZonedDateTime,
        type: Type,
        ruleCertificateType: RuleCertificateType
    ): List<Rule> = localDataSource.getRulesBy(countryIsoCode, validationClock, type, ruleCertificateType)

    override fun getRulesBy(
        countryIsoCode: String
    ): List<Rule> = localDataSource.getRulesBy(countryIsoCode)
}