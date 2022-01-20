package ro.sts.dgc.data.security

interface KeyStoreCryptor {

    fun encrypt(token: String?): String?

    fun decrypt(token: String?): String?
}