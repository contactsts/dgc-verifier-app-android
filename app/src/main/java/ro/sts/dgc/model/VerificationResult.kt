package ro.sts.dgc.model

import ro.sts.dgc.ui.model.CertificateModel
import java.time.Instant

class VerificationResult {

    /**
     * The key identifier
     */
    var kid: String? = null

    /**
     * `exp` claim SHALL hold a timestamp.
     * Verifier MUST reject the payload after expiration.
     * It MUST not exceed the validity period of the DSC.
     */
    var expirationTime: Instant? = null

    /**
     * `iat` claim SHALL hold a timestamp. It MUST not predate the validity period of the DSC.
     */
    var issuedAt: Instant? = null

    /**
     * `iss` claim MAY hold ISO 3166-1 alpha-2 country code
     */
    var issuer: String? = null

    /**
     * Lifetime of certificate used for verification of COSE
     */
    var certificateValidFrom: Instant? = null

    /**
     * Lifetime of certificate used for verification of COSE
     */
    var certificateValidUntil: Instant? = null

    /**
     * The compressed CWT is encoded as ASCII using Base45
     */
    var base45Decoded = false

    /**
     * The json representation of the hcert
     */
    var hcertJson: String = ""

    /**
     * `HC1:` SHALL be used as a prefix in the Base45 encoded data
     */
    var contextIdentifier: String? = null

    /**
     * CWT SHALL be compressed using ZLIB
     */
    var zlibDecoded = false

    /**
     * COSE signature MUST be verifiable
     */
    var coseVerified = false

    /**
     * The payload is structured and encoded as a CWT structure
     */
    var cwtDecoded = false

    /**
     * The payload is CBOR encoded
     */
    var cborDecoded = false

    /**
     * The payloads schema must be valid
     */
    var isSchemaValid = false

    /**
     * Indicates if the business rules validations have failed
     */
    var rulesValidationFailed: Boolean = false

    /**
     * Indicates which content may be signed with the certificate, defaults to all content types
     */
    var certificateValidContent: List<ContentType> = listOf(ContentType.TEST, ContentType.VACCINATION, ContentType.RECOVERY)

    /**
     * Indicates which content actually has been decoded
     */
    var contentType: MutableList<ContentType> = mutableListOf()

    /**
     * The certificate payload
     */
    var certificate: CertificateModel? = null

    fun isValid(): Boolean = base45Decoded && zlibDecoded && coseVerified && cborDecoded && isSchemaValid

    /**
     * Holds the error, if any occurred
     */
    var error: Error? = null
        set(value) {
            if (field == null) field = value
        }

    override fun toString(): String {
        return "VerificationResult(" +
                "expirationTime=$expirationTime, " +
                "issuedAt=$issuedAt, " +
                "issuer=$issuer, " +
                "base45Decoded=$base45Decoded, " +
                "contextIdentifier=$contextIdentifier, " +
                "zlibDecoded=$zlibDecoded, " +
                "coseVerified=$coseVerified, " +
                "cwtDecoded=$cwtDecoded, " +
                "cborDecoded=$cborDecoded, " +
                "isSchemaValid=$isSchemaValid, " +
                "certificateValidFrom=$certificateValidFrom, " +
                "certificateValidUntil=$certificateValidUntil, " +
                "certificateValidContent=$certificateValidContent, " +
                "contentType=$contentType" +
                "error=$error" +
                ")"
    }

}