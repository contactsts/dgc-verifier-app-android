package ro.sts.dgc.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ro.sts.dgc.data.CertificateRepository
import ro.sts.dgc.data.TrustListCertificateRepository
import ro.sts.dgc.pinning.ConfigRepository
import ro.sts.dgc.pinning.ConfigRepositoryImpl
import ro.sts.dgc.pinning.data.local.LocalConfigDataSource
import ro.sts.dgc.pinning.data.local.MutableConfigDataSource
import ro.sts.dgc.pinning.data.remote.DefaultRemoteConfigDataSource
import ro.sts.dgc.pinning.data.remote.RemoteConfigDataSource
import ro.sts.dgc.ui.data.PrivacyPolicyRepository
import ro.sts.dgc.ui.data.PrivacyPolicyRepositoryImpl
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindCertificateRepository(certificateRepository: TrustListCertificateRepository): CertificateRepository

    @Singleton
    @Binds
    abstract fun bindLocalConfigDataSource(configDataSource: LocalConfigDataSource): MutableConfigDataSource

    @Singleton
    @Binds
    abstract fun bindRemoteConfigDataSource(configDataSourceDefault: DefaultRemoteConfigDataSource): RemoteConfigDataSource

    @Singleton
    @Binds
    abstract fun bindConfigRepository(configRepository: ConfigRepositoryImpl): ConfigRepository

    @Singleton
    @Binds
    abstract fun bindPrivacyPolicyRepository(privacyPolicyRepository: PrivacyPolicyRepositoryImpl): PrivacyPolicyRepository
}