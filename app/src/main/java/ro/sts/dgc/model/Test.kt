package ro.sts.dgc.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ro.sts.dgc.schema.payload.v1.TestEntry
import java.time.Instant

@Serializable
data class Test(
    @SerialName("tg")
    val target: ValueSetEntryAdapter,

    @SerialName("tt")
    val type: ValueSetEntryAdapter,

    @SerialName("nm")
    val nameNaa: String? = null,

    @SerialName("ma")
    val nameRat: ValueSetEntryAdapter? = null,

    @SerialName("sc")
    @Serializable(with = InstantStringSerializer::class)
    val dateTimeSample: Instant,

    @SerialName("tr")
    val resultPositive: ValueSetEntryAdapter,

    @SerialName("tc")
    val testFacility: String? = null,

    @SerialName("co")
    val country: ValueSetEntryAdapter,

    @SerialName("is")
    val certificateIssuer: String,

    @SerialName("ci")
    val certificateIdentifier: String,
) {

    fun toEuSchema() = TestEntry().apply {
        tg = target.key
        tt = type.key
        nm = nameNaa
        ma = nameRat?.key
        sc = dateTimeSample
        tr = resultPositive.key
        tc = testFacility
        co = country.key
        `is` = certificateIssuer
        ci = certificateIdentifier
    }

    companion object {
        @JvmStatic
        fun fromEuSchema(it: TestEntry, valueSets: ValueSetHolder) = Test(
            target = valueSets.find(ValueSetHolder.DISEASE_AGENT_TARGETED_ID, it.tg),
            type = valueSets.find(ValueSetHolder.TEST_TYPE_ID, it.tt),
            nameNaa = it.nm,
            nameRat = it.ma?.let { valueSets.find(ValueSetHolder.TEST_MANUFACTURER_AND_NAME_ID, it) },
            dateTimeSample = it.sc,
            resultPositive = valueSets.find(ValueSetHolder.LAB_RESULT_ID, it.tr),
            testFacility = it.tc,
            country = valueSets.find(ValueSetHolder.COUNTRY_CODES_ID, it.co),
            certificateIssuer = it.`is`,
            certificateIdentifier = it.ci
        )
    }
}