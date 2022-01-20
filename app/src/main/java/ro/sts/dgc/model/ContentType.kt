package ro.sts.dgc.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ContentType {
    @SerialName("t")
    TEST,

    @SerialName("v")
    VACCINATION,

    @SerialName("r")
    RECOVERY;
}