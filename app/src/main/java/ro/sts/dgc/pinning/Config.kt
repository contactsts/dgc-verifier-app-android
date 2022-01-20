package ro.sts.dgc.pinning

import com.fasterxml.jackson.annotation.JsonProperty
import ro.sts.dgc.BuildConfig

/**
 * Holds information of public key info hashes for certificate pinning and private policy url info.
 */
data class Config(
    @JsonProperty("origin") val origin: String?,
    @JsonProperty("versions") val versions: Map<String, Version>?,
) {

    private companion object {
        const val DEFAULT_VERSION_NAME = "default"
        const val CERT_STATUS_ENDPOINT_NAME = "dsc.certificate-status"
        const val CERT_UPDATE_ENDPOINT_NAME = "dsc.certificate-update"
        const val COUNTRIES_ENDPOINT_NAME = "countryList"
        const val RULES_ENDPOINT_NAME = "rules"
        const val RULES_NATIONAL_ENDPOINT_NAME = "rules.national"
        const val VALUE_SETS_ENDPOINT_NAME = "valuesets"
    }

    private fun getCurrentVersionOrUseDefault(): Version? =
        versions?.get(BuildConfig.VERSION_NAME) ?: versions?.get(DEFAULT_VERSION_NAME)

    fun getContextUrl(): String = getCurrentVersionOrUseDefault()?.contextEndpoint?.url ?: ""

    fun getCertStatusUrl(): String = getCurrentVersionOrUseDefault()?.endpoints?.get(CERT_STATUS_ENDPOINT_NAME)?.url ?: ""

    fun getCertUpdateUrl(): String = getCurrentVersionOrUseDefault()?.endpoints?.get(CERT_UPDATE_ENDPOINT_NAME)?.url ?: ""

    fun getCountriesUrl(): String {
        return getCurrentVersionOrUseDefault()?.endpoints?.get(COUNTRIES_ENDPOINT_NAME)?.url ?: ""
    }

    fun getRulesUrl(): String = getCurrentVersionOrUseDefault()?.endpoints?.get(RULES_ENDPOINT_NAME)?.url ?: ""

    fun getNationalRulesUrl(): String = getCurrentVersionOrUseDefault()?.endpoints?.get(RULES_NATIONAL_ENDPOINT_NAME)?.url ?: ""

    fun getValueSetsUrl(): String = getCurrentVersionOrUseDefault()?.endpoints?.get(VALUE_SETS_ENDPOINT_NAME)?.url ?: ""
}

data class Endpoint(

    @JsonProperty("url")
    val url: String?,

    @JsonProperty("pubKeys")
    val pubKeys: Collection<String>?
)

data class Version(

    @JsonProperty("privacyUrl")
    val privacyUrl: String?,

    @JsonProperty("context")
    val contextEndpoint: Endpoint?,

    @JsonProperty("outdated")
    val outdated: Boolean?,

    @JsonProperty("endpoints")
    val endpoints: Map<String, Endpoint>?
)