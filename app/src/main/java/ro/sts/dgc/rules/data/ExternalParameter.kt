package ro.sts.dgc.rules.data

import ro.sts.dgc.rules.UTC_ZONE_ID
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class ExternalParameter private constructor(
    val validationClock: String,
    val valueSets: Map<String, List<String>>,
    val countryCode: String,
    val exp: String,
    val iat: String,
    val issuerCountryCode: String,
    val kid: String,
    val region: String = ""
) {
    constructor(
        validationClock: ZonedDateTime,
        valueSets: Map<String, List<String>>,
        countryCode: String,
        exp: ZonedDateTime,
        iat: ZonedDateTime,
        issuerCountryCode: String,
        kid: String,
        region: String = ""
    ) : this(
        validationClock = DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(validationClock),
        valueSets = valueSets,
        countryCode = countryCode,
        exp = exp.withZoneSameInstant(UTC_ZONE_ID).toLocalDate().toString(),
        iat = iat.withZoneSameInstant(UTC_ZONE_ID).toLocalDate().toString(),
        issuerCountryCode = issuerCountryCode,
        kid = kid,
        region = region
    )
}