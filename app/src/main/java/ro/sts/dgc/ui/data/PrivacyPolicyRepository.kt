package ro.sts.dgc.ui.data

import kotlinx.coroutines.flow.Flow

interface PrivacyPolicyRepository {

    fun getTermsAndConditions(): Flow<String?>

    companion object {
        const val TERMS_URL = ""
    }
}