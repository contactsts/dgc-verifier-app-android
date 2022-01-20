package ro.sts.dgc.rules

import com.fasterxml.jackson.databind.JsonNode
import ro.sts.dgc.rules.data.CertificateType
import ro.sts.dgc.rules.data.Rule

interface AffectedFieldsDataRetriever {
    fun getAffectedFieldsData(rule: Rule, dataJsonNode: JsonNode, certificateType: CertificateType): String
}