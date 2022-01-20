package ro.sts.dgc.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ro.sts.dgc.data.DgcDatabase
import ro.sts.dgc.rules.data.source.local.countries.CountriesDao
import ro.sts.dgc.rules.data.source.local.rules.NationalRulesDao
import ro.sts.dgc.rules.data.source.local.rules.RulesDao
import ro.sts.dgc.rules.data.source.local.valuesets.ValueSetsDao
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDb(@ApplicationContext context: Context): DgcDatabase = Room.databaseBuilder(context, DgcDatabase::class.java, "ro.sts.dgc.db").build()

    @Singleton
    @Provides
    fun provideRulesDao(dgcDatabase: DgcDatabase): RulesDao = dgcDatabase.rulesDao()

    @Singleton
    @Provides
    fun provideNationalRulesDao(dgcDatabase: DgcDatabase): NationalRulesDao = dgcDatabase.nationalRulesDao()

    @Singleton
    @Provides
    fun provideCountriesDao(dgcDatabase: DgcDatabase): CountriesDao = dgcDatabase.countriesDao()

    @Singleton
    @Provides
    fun provideValueSetsDao(dgcDatabase: DgcDatabase): ValueSetsDao = dgcDatabase.valueSetsDao()
}