package ro.sts.dgc.ui.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ro.sts.dgc.data.CertificateRepository
import ro.sts.dgc.data.Preferences
import ro.sts.dgc.pinning.ConfigRepository
import ro.sts.dgc.rules.data.countries.CountriesRepository
import ro.sts.dgc.rules.data.rules.NationalRulesRepository
import ro.sts.dgc.rules.data.rules.RulesRepository
import ro.sts.dgc.rules.data.valuesets.ValueSetsRepository
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val configRepository: ConfigRepository,
    private val certificateRepository: CertificateRepository,
    private val valueSetsRepository: ValueSetsRepository,
    private val rulesRepository: RulesRepository,
    private val countriesRepository: CountriesRepository,
    private val nationalRulesRepository: NationalRulesRepository,
    private val preferences: Preferences
) :
    ViewModel() {

    private val _inProgress = MutableLiveData<Boolean>()
    val inProgress: LiveData<Boolean> = _inProgress
    val lastSyncLiveData: LiveData<Long> = certificateRepository.getLastSyncTimeMillis()

    fun syncPublicKeys() {
        viewModelScope.launch {
            _inProgress.value = true
            withContext(Dispatchers.IO) {
                try {
                    val config = configRepository.local().getConfig()
                    certificateRepository.fetchCertificates(config.getCertStatusUrl(), config.getCertUpdateUrl())

                    rulesRepository.loadRules(config.getRulesUrl())
                    nationalRulesRepository.loadRules(config.getNationalRulesUrl())
                    countriesRepository.preLoadCountries(config.getCountriesUrl())
                    valueSetsRepository.preLoad(config.getValueSetsUrl())
                } catch (error: Throwable) {
                    Timber.e(error, "Error synchronizing keys")
                }
            }
            _inProgress.value = false
        }
    }

    fun setUserLanguage(userLanguage: String) {
        preferences.userLanguage = userLanguage
    }

    fun isUserLanguage(userLanguage: String): Boolean {
        return userLanguage == preferences.userLanguage
    }
}