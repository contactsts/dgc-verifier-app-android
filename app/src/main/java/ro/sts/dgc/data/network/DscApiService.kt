package ro.sts.dgc.data.network

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Url
import ro.sts.dgc.pinning.Config

/**
 * The API defines how to exchange verification information (Document Signer Certificates (DSCs)) for digital green certificates.
 */
interface DscApiService {

    /**
     * Gets information of public key info hashes for certificate pinning and private policy url info
     */
    @GET
    fun context(@Url url: String): Call<Config>

    /**
     * Gets one signer certificate.
     */
    @GET
    suspend fun getCertUpdate(@Header("x-resume-token") contentRange: String, @Url url: String): Response<ResponseBody>

    /**
     * Gets list of kids from all valid certificates.
     */
    @GET
    suspend fun getCertStatus(@Url url: String): Response<List<String>>
}