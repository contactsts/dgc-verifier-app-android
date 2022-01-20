package ro.sts.dgc.rules.data.source.remote.rules

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode
import java.time.ZonedDateTime

data class RuleRemote(
    @JsonProperty("Identifier")
    val identifier: String,
    @JsonProperty("Type")
    val type: String,
    @JsonProperty("Version")
    val version: String,
    @JsonProperty("SchemaVersion")
    val schemaVersion: String,
    @JsonProperty("Engine")
    val engine: String,
    @JsonProperty("EngineVersion")
    val engineVersion: String,
    @JsonProperty("CertificateType")
    val certificateType: String,
    @JsonProperty("Description")
    val descriptions: List<DescriptionRemote>,
    @JsonProperty("ValidFrom")
    val validFrom: ZonedDateTime,
    @JsonProperty("ValidTo")
    val validTo: ZonedDateTime,
    @JsonProperty("AffectedFields")
    val affectedString: List<String>,
    @JsonProperty("Logic")
    val logic: JsonNode,
    @JsonProperty("Country")
    val countryCode: String,
    @JsonProperty("Region")
    val region: String?
)