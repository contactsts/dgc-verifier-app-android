package ro.sts.dgc.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ro.sts.dgc.data.security.DefaultKeyStoreCryptor
import ro.sts.dgc.data.security.KeyStoreCryptor
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class SecurityModule {

    @Singleton
    @Binds
    abstract fun bindKeyStoreCryptor(keyStoreCryptor: DefaultKeyStoreCryptor): KeyStoreCryptor
}