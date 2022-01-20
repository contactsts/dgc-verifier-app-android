package ro.sts.dgc.ui.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import ro.sts.dgc.ui.data.PrivacyPolicyRepository
import ro.sts.dgc.util.HandledException
import javax.inject.Inject

@HiltViewModel
class PrivacyPolicyViewModel @Inject constructor(
    private val privacyPolicyRepository: PrivacyPolicyRepository
) : ViewModel(), CoroutineScope {

    private val _errorLiveData = MutableLiveData<HandledException>()

    private val _privacyPolicyContentLiveData = MutableLiveData<String>()

    val privacyPolicyContentLiveData: LiveData<String?>
        get() {
            return _privacyPolicyContentLiveData
        }

    fun getPrivacyPolicy() = launch {
        privacyPolicyRepository.getTermsAndConditions()
            .flowOn(Dispatchers.IO)
            .collect {
                _privacyPolicyContentLiveData.postValue(it)
            }
    }

    /**
     * Sends an error to default error-handling LiveData
     */
    private fun postError(error: HandledException) = _errorLiveData.postValue(error)

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, e ->
        if (e is HandledException) {
            postError(e)
        } else {
            throw e
        }
    }

    override val coroutineContext = viewModelScope.coroutineContext + coroutineExceptionHandler
}