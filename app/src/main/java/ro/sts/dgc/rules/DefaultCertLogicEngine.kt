package ro.sts.dgc.rules

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.readValue
import ro.sts.dgc.rules.data.CertificateType
import ro.sts.dgc.rules.data.ExternalParameter
import ro.sts.dgc.rules.data.Rule

class DefaultCertLogicEngine(private val affectedFieldsDataRetriever: AffectedFieldsDataRetriever, private val jsonLogicValidator: JsonLogicValidator) : CertLogicEngine {
    private val objectMapper = ObjectMapper()

    companion object {
        private const val EXTERNAL_KEY = "external"
        private const val PAYLOAD_KEY = "payload"
        private const val CERTLOGIC_KEY = "CERTLOGIC"
        private val CERTLOGIC_VERSION: Triple<Int, Int, Int> = Triple(1, 0, 0)
    }

    init {
        objectMapper.findAndRegisterModules()
    }

    private fun prepareData(
        externalParameter: ExternalParameter,
        payload: String
    ): ObjectNode = objectMapper.createObjectNode().apply {
        this.set<JsonNode>(EXTERNAL_KEY, objectMapper.readValue(objectMapper.writeValueAsString(externalParameter)))
        this.set<JsonNode>(PAYLOAD_KEY, objectMapper.readValue<JsonNode>(payload))
    }

    override fun validate(
        certificateType: CertificateType,
        hcertVersionString: String,
        rules: List<Rule>,
        externalParameter: ExternalParameter,
        payload: String
    ): List<ValidationResult> {
        return if (rules.isNotEmpty()) {
            val validationResults = mutableListOf<ValidationResult>()
            val dataJsonNode = prepareData(externalParameter, payload)
            val hcertVersion = hcertVersionString.toVersion()
            rules.forEach { rule ->
                val ruleEngineVersion = rule.engineVersion.toVersion()
                val schemaVersion = rule.schemaVersion.toVersion()
                val res = when {
                    rule.engine == CERTLOGIC_KEY && ruleEngineVersion != null && CERTLOGIC_VERSION.isGreaterOrEqualThan(
                        ruleEngineVersion
                    ) && hcertVersion != null && schemaVersion != null && hcertVersion.first == schemaVersion.first && hcertVersion.isGreaterOrEqualThan(
                        schemaVersion
                    ) ->
                        when (jsonLogicValidator.isDataValid(
                            rule.logic,
                            dataJsonNode
                        )) {
                            true -> Result.PASSED
                            false -> Result.FAIL
                            else -> Result.OPEN
                        }
                    else -> Result.OPEN
                }
                val cur: String = affectedFieldsDataRetriever.getAffectedFieldsData(
                    rule,
                    dataJsonNode,
                    certificateType
                )
                validationResults.add(
                    ValidationResult(
                        rule,
                        res,
                        cur,
                        null
                    )
                )
            }
            validationResults
        } else {
            emptyList()
        }
    }

    private fun Triple<Int, Int, Int>.isGreaterOrEqualThan(version: Triple<Int, Int, Int>): Boolean =
        first > version.first || (first == version.first && (second > version.second || (second == version.second && third >= version.third)))

    /**
     * Tries to convert String into a version based on pattern majorVersion.minorVersion.patchVersion.
     */
    private fun String.toVersion(): Triple<Int, Int, Int>? = try {
        val versionPieces = this.split('.')
        Triple(versionPieces[0].toInt(), versionPieces[1].toInt(), versionPieces[2].toInt())
    } catch (error: Throwable) {
        null
    }
}