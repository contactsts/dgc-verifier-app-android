package ro.sts.dgc.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ro.sts.dgc.schema.payload.v1.VaccinationEntry
import java.time.LocalDate

@Serializable
data class Vaccination(
    @SerialName("tg")
    val target: ValueSetEntryAdapter,

    @SerialName("vp")
    val vaccine: ValueSetEntryAdapter,

    @SerialName("mp")
    val medicinalProduct: ValueSetEntryAdapter,

    @SerialName("ma")
    val authorizationHolder: ValueSetEntryAdapter,

    @SerialName("dn")
    val doseNumber: Int,

    @SerialName("sd")
    val doseTotalNumber: Int,

    @SerialName("dt")
    @Serializable(with = LocalDateSerializer::class)
    val date: LocalDate,

    @SerialName("co")
    val country: ValueSetEntryAdapter,

    @SerialName("is")
    val certificateIssuer: String,

    @SerialName("ci")
    val certificateIdentifier: String,
) {

    fun toEuSchema() = VaccinationEntry().apply {
        tg = target.key
        vp = vaccine.key
        mp = medicinalProduct.key
        ma = authorizationHolder.key
        dn = doseNumber
        sd = doseTotalNumber
        dt = date
        co = country.key
        `is` = certificateIssuer
        ci = certificateIdentifier
    }

    companion object {
        @JvmStatic
        fun fromEuSchema(it: VaccinationEntry, valueSets: ValueSetHolder) = Vaccination(
            target = valueSets.find(ValueSetHolder.DISEASE_AGENT_TARGETED_ID, it.tg),
            vaccine = valueSets.find(ValueSetHolder.VACCINE_PROPHYLAXIS_ID, it.vp),
            medicinalProduct = valueSets.find(ValueSetHolder.MEDICAL_PRODUCT_ID, it.mp),
            authorizationHolder = valueSets.find(ValueSetHolder.MARKETING_AUTH_HOLDERS_ID, it.ma),
            doseNumber = it.dn,
            doseTotalNumber = it.sd,
            date = it.dt,
            country = valueSets.find(ValueSetHolder.COUNTRY_CODES_ID, it.co),
            certificateIssuer = it.`is`,
            certificateIdentifier = it.ci
        )
    }
}