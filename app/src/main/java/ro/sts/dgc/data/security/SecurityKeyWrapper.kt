package ro.sts.dgc.data.security

import android.util.Base64
import timber.log.Timber
import java.security.GeneralSecurityException
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Wrapper for {@SecretKey} that provide ability to encrypt/decrypt data using it.
 */
class SecurityKeyWrapper(private val secretKey: SecretKey) {

    /**
     * Encrypt token and return Base64 encoded value
     */
    fun encrypt(token: String?): String? {
        if (token == null) return null
        try {
            val cipher = getCipher(Cipher.ENCRYPT_MODE)
            val encrypted = cipher.doFinal(token.toByteArray())
            return Base64.encodeToString(encrypted, Base64.URL_SAFE)
        } catch (e: GeneralSecurityException) {
            Timber.w(e)
        }
        return null
    }

    /**
     * Decrypt token from Base64 encoded value
     */
    fun decrypt(encryptedToken: String?): String? {
        if (encryptedToken == null) return null
        try {
            val cipher = getCipher(Cipher.DECRYPT_MODE)
            val decoded = Base64.decode(encryptedToken, Base64.URL_SAFE)
            val original = cipher.doFinal(decoded)
            return String(original)
        } catch (e: GeneralSecurityException) {
            Timber.w(e)
        }
        return null
    }

    @Throws(GeneralSecurityException::class)
    private fun getCipher(mode: Int) = Cipher.getInstance(AES_GCM_NO_PADDING).apply {
        init(mode, secretKey, GCMParameterSpec(128, AES_GCM_NO_PADDING.toByteArray(), 0, 12))
    }

    companion object {
        private const val AES_GCM_NO_PADDING = "AES/GCM/NoPadding"
    }
}