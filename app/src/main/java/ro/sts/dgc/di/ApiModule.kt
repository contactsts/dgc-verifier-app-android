package ro.sts.dgc.di

import android.content.Context
import com.fasterxml.jackson.databind.ObjectMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import ro.sts.dgc.BuildConfig
import ro.sts.dgc.data.network.DscApiService
import ro.sts.dgc.data.network.HeaderInterceptor
import ro.sts.dgc.pinning.ConfigRepository
import ro.sts.dgc.rules.data.source.remote.countries.CountriesApiService
import ro.sts.dgc.rules.data.source.remote.rules.NationalRulesApiService
import ro.sts.dgc.rules.data.source.remote.rules.RulesApiService
import ro.sts.dgc.rules.data.source.remote.valuesets.ValueSetsApiService
import java.net.URL
import java.util.concurrent.TimeUnit
import javax.inject.Provider
import javax.inject.Singleton

private const val CONNECT_TIMEOUT = 30L

const val BASE_URL = BuildConfig.BASE_URL
const val SHA256_PREFIX = "sha256/"

@InstallIn(SingletonComponent::class)
@Module
object ApiModule {

    @Singleton
    @Provides
    internal fun provideObjectMapper(): ObjectMapper = ObjectMapper().apply { findAndRegisterModules() }

    @Singleton
    @Provides
    internal fun provideConverterFactory(objectMapper: ObjectMapper): Converter.Factory = JacksonConverterFactory.create(objectMapper)

    @Singleton
    @Provides
    internal fun provideCache(@ApplicationContext context: Context): Cache {
        val cacheSize = (10 * 1024 * 1024).toLong() // 10 MB
        return Cache(context.cacheDir, cacheSize)
    }

    @Singleton
    @Provides
    internal fun provideHeaderInterceptor(): Interceptor = HeaderInterceptor()

    @Provides
    internal fun provideCertificatePinner(configRepository: ConfigRepository): CertificatePinner {
        val config = configRepository.local().getConfig()
        val certificatePinnerBuilder = CertificatePinner.Builder()
        config.versions?.values?.let { versions ->
            versions.forEach { version ->
                when (version.outdated) {
                    true -> {
                    }
                    else -> {
                        version.contextEndpoint?.pubKeys?.forEach { keyHash ->
                            certificatePinnerBuilder.add(URL(version.contextEndpoint.url).host, "$SHA256_PREFIX$keyHash")
                        }
                        version.endpoints?.values?.forEach { endpoint ->
                            endpoint.pubKeys?.forEach { keyHash ->
                                certificatePinnerBuilder.add(URL(endpoint.url).host, "$SHA256_PREFIX$keyHash")
                            }
                        }
                    }
                }
            }
        }
        return certificatePinnerBuilder.build()
    }

    @Singleton
    @Provides
    internal fun provideOkhttpClient(cache: Cache, interceptor: Interceptor, certificatePinner: CertificatePinner): OkHttpClient {
        val httpClient = getHttpClient(cache).apply {
            addInterceptor(HeaderInterceptor())
            certificatePinner(certificatePinner)
        }
        addLogging(httpClient)

        return httpClient.build()
    }

    @Singleton
    @Provides
    internal fun provideRetrofit(converterFactory: Converter.Factory, okHttpClient: Provider<OkHttpClient>): Retrofit {
        return createRetrofit(converterFactory, okHttpClient)
    }

    @Singleton
    @Provides
    internal fun provideDscApiService(retrofit: Retrofit): DscApiService {
        return retrofit.create(DscApiService::class.java)
    }

    @Singleton
    @Provides
    internal fun provideRulesApiService(retrofit: Retrofit): RulesApiService {
        return retrofit.create(RulesApiService::class.java)
    }

    @Singleton
    @Provides
    internal fun provideNationalRulesApiService(retrofit: Retrofit): NationalRulesApiService {
        return retrofit.create(NationalRulesApiService::class.java)
    }

    @Singleton
    @Provides
    internal fun provideCountriesApiService(retrofit: Retrofit): CountriesApiService {
        return retrofit.create(CountriesApiService::class.java)
    }

    @Singleton
    @Provides
    internal fun provideValueSetsApiService(retrofit: Retrofit): ValueSetsApiService {
        return retrofit.create(ValueSetsApiService::class.java)
    }

    private fun getHttpClient(cache: Cache): OkHttpClient.Builder {
        return OkHttpClient.Builder()
            .cache(cache)
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
    }

    private fun addLogging(httpClient: OkHttpClient.Builder) {
        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BASIC
            httpClient.addInterceptor(logging)
        }
    }

    private fun createRetrofit(converterFactory: Converter.Factory, okHttpClient: Provider<OkHttpClient>): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(converterFactory)
            .baseUrl(BASE_URL)
            .callFactory { okHttpClient.get().newCall(it) }
            .build()
    }
}

@PublishedApi
internal inline fun Retrofit.Builder.callFactory(crossinline body: (Request) -> Call) = callFactory(object : Call.Factory {
    override fun newCall(request: Request): Call = body(request)
})
