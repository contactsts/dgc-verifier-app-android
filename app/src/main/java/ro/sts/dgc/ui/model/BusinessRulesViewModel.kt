package ro.sts.dgc.ui.model

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ro.sts.dgc.rules.data.Rule
import ro.sts.dgc.rules.data.countries.CountriesRepository
import ro.sts.dgc.rules.domain.GetRulesUseCase
import ro.sts.dgc.ui.countrypicker.Country
import javax.inject.Inject

@HiltViewModel
class BusinessRulesViewModel @Inject constructor(
    private val countriesRepository: CountriesRepository,
    private val getRulesUseCase: GetRulesUseCase,
) : ViewModel() {
    val countries: LiveData<List<String>> = countriesRepository.getCountries().asLiveData()

    private val _rules = MutableLiveData<List<Rule>>()
    val rules: LiveData<List<Rule>> = _rules

    private val _inProgress = MutableLiveData<Boolean>()
    val inProgress: LiveData<Boolean> = _inProgress

    fun selectCountry(country: Country) {
        viewModelScope.launch {
            _inProgress.value = true

            withContext(Dispatchers.IO) {
                _rules.postValue(getRulesUseCase.invoke(country.countryCode))
            }
            _inProgress.value = false
        }
    }
}