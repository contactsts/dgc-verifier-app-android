package ro.sts.dgc.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ro.sts.dgc.cose.CoseService
import ro.sts.dgc.cose.DefaultCoseService
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class CoseModule {

    @Singleton
    @Binds
    abstract fun bindCoseService(coseService: DefaultCoseService): CoseService

}