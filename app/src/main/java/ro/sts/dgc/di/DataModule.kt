package ro.sts.dgc.di

import android.content.Context
import com.fasterxml.jackson.databind.ObjectMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ro.sts.dgc.data.Preferences
import ro.sts.dgc.data.PreferencesImpl
import ro.sts.dgc.rules.*
import ro.sts.dgc.rules.data.countries.CountriesRepository
import ro.sts.dgc.rules.data.countries.DefaultCountriesRepository
import ro.sts.dgc.rules.data.rules.DefaultNationalRulesRepository
import ro.sts.dgc.rules.data.rules.DefaultRulesRepository
import ro.sts.dgc.rules.data.rules.NationalRulesRepository
import ro.sts.dgc.rules.data.rules.RulesRepository
import ro.sts.dgc.rules.data.source.local.countries.CountriesDao
import ro.sts.dgc.rules.data.source.local.countries.CountriesLocalDataSource
import ro.sts.dgc.rules.data.source.local.countries.DefaultCountriesLocalDataSource
import ro.sts.dgc.rules.data.source.local.rules.*
import ro.sts.dgc.rules.data.source.local.valuesets.DefaultValueSetsLocalDataSource
import ro.sts.dgc.rules.data.source.local.valuesets.ValueSetsDao
import ro.sts.dgc.rules.data.source.local.valuesets.ValueSetsLocalDataSource
import ro.sts.dgc.rules.data.source.remote.countries.CountriesApiService
import ro.sts.dgc.rules.data.source.remote.countries.CountriesRemoteDataSource
import ro.sts.dgc.rules.data.source.remote.countries.DefaultCountriesRemoteDataSource
import ro.sts.dgc.rules.data.source.remote.rules.*
import ro.sts.dgc.rules.data.source.remote.valuesets.DefaultValueSetsRemoteDataSource
import ro.sts.dgc.rules.data.source.remote.valuesets.ValueSetsApiService
import ro.sts.dgc.rules.data.source.remote.valuesets.ValueSetsRemoteDataSource
import ro.sts.dgc.rules.data.valuesets.DefaultValueSetsRepository
import ro.sts.dgc.rules.data.valuesets.ValueSetsRepository
import ro.sts.dgc.rules.domain.DefaultGetRulesUseCase
import ro.sts.dgc.rules.domain.GetRulesUseCase
import ro.sts.dgc.schema.JSON_SCHEMA_V1
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DataModule {

    @Singleton
    @Provides
    fun providePreferences(@ApplicationContext context: Context): Preferences = PreferencesImpl(context)

    @Singleton
    @Provides
    fun provideCryptoService(): JsonLogicValidator = DefaultJsonLogicValidator()

    @Singleton
    @Provides
    fun provideAffectedFieldsDataRetriever(objectMapper: ObjectMapper): AffectedFieldsDataRetriever = DefaultAffectedFieldsDataRetriever(objectMapper.readTree(JSON_SCHEMA_V1), objectMapper)

    @Singleton
    @Provides
    fun provideCertLogicEngine(affectedFieldsDataRetriever: AffectedFieldsDataRetriever, jsonLogicValidator: JsonLogicValidator): CertLogicEngine =
        DefaultCertLogicEngine(affectedFieldsDataRetriever, jsonLogicValidator)

    @Singleton
    @Provides
    fun provideRulesLocalDataSource(rulesDao: RulesDao): RulesLocalDataSource = DefaultRulesLocalDataSource(rulesDao)

    @Singleton
    @Provides
    fun provideRulesRemoteDataSource(rulesApiService: RulesApiService): RulesRemoteDataSource = DefaultRulesRemoteDataSource(rulesApiService)

    @Singleton
    @Provides
    fun provideRulesRepository(remoteDataSource: RulesRemoteDataSource, localDataSource: RulesLocalDataSource): RulesRepository = DefaultRulesRepository(remoteDataSource, localDataSource)

    @Singleton
    @Provides
    fun provideNationalRulesLocalDataSource(nationalRulesDao: NationalRulesDao): NationalRulesLocalDataSource = DefaultNationalRulesLocalDataSource(nationalRulesDao)

    @Singleton
    @Provides
    fun provideNationalRulesRemoteDataSource(nationalRulesApiService: NationalRulesApiService): NationalRulesRemoteDataSource = DefaultNationalRulesRemoteDataSource(nationalRulesApiService)

    @Singleton
    @Provides
    fun provideNationalRulesRepository(remoteDataSource: NationalRulesRemoteDataSource, localDataSource: NationalRulesLocalDataSource): NationalRulesRepository =
        DefaultNationalRulesRepository(remoteDataSource, localDataSource)

    @Singleton
    @Provides
    fun provideCountriesLocalDataSource(countriesDao: CountriesDao): CountriesLocalDataSource = DefaultCountriesLocalDataSource(countriesDao)

    @Singleton
    @Provides
    fun provideCountriesRemoteDataSource(countriesApiService: CountriesApiService): CountriesRemoteDataSource = DefaultCountriesRemoteDataSource(countriesApiService)

    @Singleton
    @Provides
    fun provideCountriesRepository(remoteDataSource: CountriesRemoteDataSource, localDataSource: CountriesLocalDataSource): CountriesRepository =
        DefaultCountriesRepository(remoteDataSource, localDataSource)

    @Singleton
    @Provides
    fun provideValueSetsLocalDataSource(dao: ValueSetsDao): ValueSetsLocalDataSource = DefaultValueSetsLocalDataSource(dao)

    @Singleton
    @Provides
    fun provideValueSetsRemoteDataSource(apiService: ValueSetsApiService): ValueSetsRemoteDataSource =
        DefaultValueSetsRemoteDataSource(apiService)

    @Singleton
    @Provides
    fun provideValueSetsRepository(remoteDataSource: ValueSetsRemoteDataSource, localDataSource: ValueSetsLocalDataSource): ValueSetsRepository =
        DefaultValueSetsRepository(remoteDataSource, localDataSource)

    @Singleton
    @Provides
    fun provideGetRulesUseCase(rulesRepository: RulesRepository, nationalRulesRepository: NationalRulesRepository): GetRulesUseCase = DefaultGetRulesUseCase(rulesRepository, nationalRulesRepository)
}