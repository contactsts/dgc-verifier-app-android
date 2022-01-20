package ro.sts.dgc.rules

import ro.sts.dgc.rules.data.CertificateType
import ro.sts.dgc.rules.data.ExternalParameter
import ro.sts.dgc.rules.data.Rule

interface CertLogicEngine {
    fun validate(certificateType: CertificateType, hcertVersionString: String, rules: List<Rule>, externalParameter: ExternalParameter, payload: String): List<ValidationResult>
}