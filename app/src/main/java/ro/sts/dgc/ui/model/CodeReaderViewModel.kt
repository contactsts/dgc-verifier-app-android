package ro.sts.dgc.ui.model

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import ro.sts.dgc.data.CertificateRepository
import ro.sts.dgc.data.Preferences
import ro.sts.dgc.rules.data.countries.CountriesRepository
import javax.inject.Inject

@HiltViewModel
class CodeReaderViewModel @Inject constructor(
    private val countriesRepository: CountriesRepository,
    private val preferences: Preferences,
    private val certificateRepository: CertificateRepository
) : ViewModel() {
    private val _countries: MediatorLiveData<Pair<List<String>, String?>> = MediatorLiveData()
    private val _selectedCountry: LiveData<String?> = liveData {
        emit(preferences.selectedCountryIsoCode)
    }
    private val _useNationalRules = MutableLiveData<Boolean>()

    init {
        _useNationalRules.value = preferences.useNationalRules
    }

    val countries: LiveData<Pair<List<String>, String?>> = _countries

    fun selectCountry(countryIsoCode: String) {
        preferences.selectedCountryIsoCode = countryIsoCode
    }

    val useNationalRules: LiveData<Boolean> = _useNationalRules

    fun setUseNationalRules(useNationalRules: Boolean) {
        preferences.useNationalRules = useNationalRules
        _useNationalRules.value = useNationalRules
    }

    val lastSyncLiveData: LiveData<Long> = certificateRepository.getLastSyncTimeMillis()

    init {
        _countries.addSource(countriesRepository.getCountries().asLiveData()) {
            _countries.value = Pair(it, _countries.value?.second)
        }

        _countries.addSource(_selectedCountry) {
            if (_countries.value?.second == null || _countries.value?.second != it) {
                _countries.value = Pair(_countries.value?.first ?: emptyList(), it ?: "")
            }
        }
    }
}