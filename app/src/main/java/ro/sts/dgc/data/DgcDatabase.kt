package ro.sts.dgc.data

import androidx.room.*
import androidx.room.migration.AutoMigrationSpec
import ro.sts.dgc.rules.data.source.local.countries.CountriesDao
import ro.sts.dgc.rules.data.source.local.countries.CountryLocal
import ro.sts.dgc.rules.data.source.local.rules.*
import ro.sts.dgc.rules.data.source.local.valuesets.ValueSetIdentifierLocal
import ro.sts.dgc.rules.data.source.local.valuesets.ValueSetLocal
import ro.sts.dgc.rules.data.source.local.valuesets.ValueSetsDao

@Database(
    entities = [
        DscCertificate::class,
        RuleIdentifierLocal::class,
        RuleLocal::class,
        DescriptionLocal::class,
        CountryLocal::class,
        ValueSetLocal::class,
        ValueSetIdentifierLocal::class,
        NationalRuleIdentifierLocal::class,
        NationalRuleLocal::class,
        NationalDescriptionLocal::class
    ],
    version = 5,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3),
        AutoMigration(from = 3, to = 4, spec = DgcDatabase.Migration3to4::class),
        AutoMigration(from = 4, to = 5),
    ]
)
@TypeConverters(Converters::class)
abstract class DgcDatabase : RoomDatabase() {

    abstract fun dscCertificateDao(): DscCertificateDao

    abstract fun rulesDao(): RulesDao

    abstract fun nationalRulesDao(): NationalRulesDao

    abstract fun countriesDao(): CountriesDao

    abstract fun valueSetsDao(): ValueSetsDao

    @DeleteColumn(tableName = "valuesets", columnName = "id")
    class Migration3to4 : AutoMigrationSpec {}
}