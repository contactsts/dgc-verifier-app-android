package ro.sts.dgc.ui.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ro.sts.dgc.cbor.CborService
import ro.sts.dgc.compression.CompressorService
import ro.sts.dgc.cose.CoseService
import ro.sts.dgc.cwt.CwtService
import ro.sts.dgc.data.Preferences
import ro.sts.dgc.encoding.Base45Service
import ro.sts.dgc.model.*
import ro.sts.dgc.prefix.PrefixValidationService
import ro.sts.dgc.rules.CertLogicEngine
import ro.sts.dgc.rules.Result
import ro.sts.dgc.rules.UTC_ZONE_ID
import ro.sts.dgc.rules.ValidationResult
import ro.sts.dgc.rules.data.CertificateType
import ro.sts.dgc.rules.data.ExternalParameter
import ro.sts.dgc.rules.data.valuesets.ValueSetsRepository
import ro.sts.dgc.rules.domain.GetRulesUseCase
import ro.sts.dgc.schema.SchemaValidator
import timber.log.Timber
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CodeVerificationViewModel @Inject constructor(
    private val prefixValidationService: PrefixValidationService,
    private val base45Service: Base45Service,
    private val compressorService: CompressorService,
    private val coseService: CoseService,
    private val cwtService: CwtService,
    private val cborService: CborService,
    private val schemaValidator: SchemaValidator,
    private val engine: CertLogicEngine,
    private val getRulesUseCase: GetRulesUseCase,
    private val valueSetsRepository: ValueSetsRepository,
    private val preferences: Preferences
) : ViewModel() {

    private val _verificationResult = MutableLiveData<VerificationResult>()
    val verificationResult: LiveData<VerificationResult> = _verificationResult

    private val _certificate = MutableLiveData<CertificateModel?>()
    val certificate: LiveData<CertificateModel?> = _certificate

    private val _rulesValidationResults = MutableLiveData<List<ValidationResult>>()
    val rulesValidationResults: LiveData<List<ValidationResult>> = _rulesValidationResults

    private val _inProgress = MutableLiveData<Boolean>()
    val inProgress: LiveData<Boolean> = _inProgress

    fun init(qrCodeText: String, countryIsoCode: String) {
        decode(qrCodeText, countryIsoCode)
    }

    private fun decode(code: String, countryIsoCode: String) {
        viewModelScope.launch {
            _inProgress.value = true
            var greenCertificate: GreenCertificate? = null
            val verificationResult = VerificationResult()
            var isApplicableCode = false

            withContext(Dispatchers.IO) {
                val encoded = prefixValidationService.decode(code, verificationResult)
                if (verificationResult.contextIdentifier == null) {
                    Timber.d("Verification failed: Not applicable DCC code")
                    return@withContext
                }
                isApplicableCode = true
                val compressed = base45Service.decode(encoded, verificationResult)
                val cose = compressorService.decode(compressed, verificationResult)
                val cwt = coseService.decode(cose, verificationResult)
                val cbor = cwtService.decode(cwt, verificationResult)
                val eudgc = cborService.decode(cbor, verificationResult)

                schemaValidator.validate(cbor, verificationResult)
                val valueSets = ValueSetHolder.getValueSetHolder(valueSetsRepository)

                greenCertificate = GreenCertificate.fromEuSchema(eudgc, valueSets)
                verificationResult.certificate = greenCertificate?.toCertificateModel()

                if (verificationResult.isValid() && verificationResult.kid?.isNotBlank() == true) {
                    verificationResult.certificate?.validateRules(verificationResult, countryIsoCode, verificationResult.kid!!, preferences.useNationalRules)
                }
            }
            _inProgress.value = false
            _verificationResult.value = if (isApplicableCode) verificationResult else null
            _certificate.value = greenCertificate?.toCertificateModel()
        }
    }

    private suspend fun CertificateModel.validateRules(
        verificationResult: VerificationResult,
        countryIsoCode: String,
        base64EncodedKid: String,
        useNationalRules: Boolean
    ) {
        this.apply {
            val engineCertificateType = this.getEngineCertificateType()
            if (countryIsoCode.isNotBlank()) {
                val issuingCountry: String =
                    (if (verificationResult.issuer?.isNotBlank() == true && verificationResult.issuer != null) verificationResult.issuer!! else this.getIssuingCountry()).lowercase(Locale.ROOT)

                val rules = getRulesUseCase.invoke(
                    ZonedDateTime.now().withZoneSameInstant(UTC_ZONE_ID),
                    countryIsoCode,
                    issuingCountry,
                    engineCertificateType,
                    null,
                    useNationalRules
                )
                val valueSetsMap = mutableMapOf<String, List<String>>()
                valueSetsRepository.getValueSets().forEach { valueSet ->
                    val ids = mutableListOf<String>()
                    valueSet.valueSetValues.fieldNames().forEach { id -> ids.add(id) }
                    valueSetsMap[valueSet.valueSetId] = ids
                }

                val externalParameter = ExternalParameter(
                    validationClock = ZonedDateTime.now(ZoneId.of(ZoneOffset.UTC.id)),
                    valueSets = valueSetsMap,
                    countryCode = countryIsoCode,
                    exp = verificationResult.expirationTime?.atZone(ZoneOffset.UTC)!!,
                    iat = verificationResult.issuedAt?.atZone(ZoneOffset.UTC)!!,
                    issuerCountryCode = issuingCountry,
                    kid = base64EncodedKid,
                    region = "",
                )
                val validationResults = engine.validate(
                    engineCertificateType,
                    this.schemaVersion,
                    rules,
                    externalParameter,
                    verificationResult.hcertJson
                )

                _rulesValidationResults.postValue(validationResults)

                validationResults.forEach {
                    if (it.result != Result.PASSED) {
                        verificationResult.rulesValidationFailed = true
                        return@forEach
                    }
                }
            }
        }
    }

    private fun CertificateModel.getEngineCertificateType(): CertificateType {
        return when {
            this.recoveryStatements?.isNotEmpty() == true -> CertificateType.RECOVERY
            this.vaccinations?.isNotEmpty() == true -> CertificateType.VACCINATION
            this.tests?.isNotEmpty() == true -> CertificateType.TEST
            else -> CertificateType.TEST
        }
    }

}