package ro.sts.dgc.rules

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.NullNode
import ro.sts.dgc.certlogic.evaluate
import ro.sts.dgc.rules.data.CertificateType
import ro.sts.dgc.rules.data.Rule

class DefaultAffectedFieldsDataRetriever(private val schemaJsonNode: JsonNode, private val objectMapper: ObjectMapper) : AffectedFieldsDataRetriever {
    override fun getAffectedFieldsData(rule: Rule, dataJsonNode: JsonNode, certificateType: CertificateType): String {
        var affectedFields = StringBuilder()
        rule.affectedString.forEach { affectedFiledString ->
            val description: String? = try {
                val res = evaluate(
                    objectMapper.readTree(
                        "{\"var\": \"${
                            certificateType.getSchemaPath(affectedFiledString.split('.').last())
                        }\"}"
                    ),
                    schemaJsonNode
                )
                if (res is NullNode) null else res.toPrettyString()
            } catch (error: Throwable) {
                null
            }
            val value: String? = try {
                evaluate(objectMapper.readTree("{\"var\": \"payload.$affectedFiledString\"}"), dataJsonNode).toPrettyString()
            } catch (error: Throwable) {
                null
            }
            if (description?.isNotBlank() == true && value?.isNotBlank() == true) {
                affectedFields = affectedFields.append("$description: $value\n")
            }
        }

        return affectedFields.toString()
    }
}

private fun CertificateType.getSchemaPath(key: String): String {
    val subPath = when (this) {
        CertificateType.TEST -> "test_entry"
        CertificateType.RECOVERY -> "recovery_entry"
        CertificateType.VACCINATION -> "vaccination_entry"
    }
    return "\$defs.$subPath.properties.$key.description"
}