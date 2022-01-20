package ro.sts.dgc.rules.data

enum class RuleCertificateType {
    GENERAL, TEST, VACCINATION, RECOVERY
}

enum class CertificateType {
    TEST, VACCINATION, RECOVERY;

    fun toRuleCertificateType(): RuleCertificateType = when (this) {
        TEST -> RuleCertificateType.TEST
        VACCINATION -> RuleCertificateType.VACCINATION
        RECOVERY -> RuleCertificateType.RECOVERY
    }
}