package ro.sts.dgc.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ro.sts.dgc.cbor.CborService
import ro.sts.dgc.cbor.DefaultCborService
import ro.sts.dgc.compression.CompressorService
import ro.sts.dgc.compression.DefaultCompressorService
import ro.sts.dgc.cose.CoseService
import ro.sts.dgc.cose.DefaultCoseService
import ro.sts.dgc.cwt.CwtService
import ro.sts.dgc.cwt.DefaultCwtService
import ro.sts.dgc.encoding.Base45Service
import ro.sts.dgc.encoding.DefaultBase45Service
import ro.sts.dgc.prefix.DefaultPrefixValidationService
import ro.sts.dgc.prefix.PrefixValidationService
import ro.sts.dgc.schema.DefaultSchemaValidator
import ro.sts.dgc.schema.SchemaValidator
import javax.inject.Singleton

/**
 * Provide QR decoder functionality for injection.
 */
@InstallIn(SingletonComponent::class)
@Module
object DecoderModule {

    @Singleton
    @Provides
    fun providePrefixValidationService(): PrefixValidationService = DefaultPrefixValidationService()

    @ExperimentalUnsignedTypes
    @Singleton
    @Provides
    fun provideBase45Decoder(): Base45Service = DefaultBase45Service()

    @Singleton
    @Provides
    fun provideCompressorService(): CompressorService = DefaultCompressorService()
//
//    @Singleton
//    @Provides
//    fun provideCoseService(): CoseService = DefaultCoseService()

    @Singleton
    @Provides
    fun provideCwtService(): CwtService = DefaultCwtService()

    @Singleton
    @Provides
    fun provideSchemaValidator(): SchemaValidator = DefaultSchemaValidator()

    @Singleton
    @Provides
    fun provideCborService(): CborService = DefaultCborService()
}