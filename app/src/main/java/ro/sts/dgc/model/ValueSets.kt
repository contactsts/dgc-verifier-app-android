package ro.sts.dgc.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import ro.sts.dgc.rules.data.valuesets.ValueSetsRepository
import java.time.LocalDate
import javax.inject.Inject

/**
 * Holds a list of value sets, that get lazily loaded from the JSON files in "src/main/resources/value-sets".
 */
data class ValueSetHolder @Inject constructor(val valueSets: List<ValueSet>) {

    fun find(valueSetId: String, key: String): ValueSetEntryAdapter {
        return valueSets.firstOrNull {
            it.valueSetId == valueSetId
        }?.valueSetValues?.get(key)?.let {
            ValueSetEntryAdapter(key, it)
        } ?: return buildMissingValueSetEntry(key, valueSetId)
    }

    fun find(key: String): ValueSetEntryAdapter {
        valueSets.forEach {
            if (it.valueSetValues.containsKey(key)) {
                return ValueSetEntryAdapter(key, it.valueSetValues[key]!!)
            }
        }
        return buildMissingValueSetEntry(key, null)
    }

    private fun buildMissingValueSetEntry(key: String, valueSetId: String?): ValueSetEntryAdapter {
        val entry = ValueSetEntry(key, "en", false, "http://example.com/missing", "0", valueSetId)
        return ValueSetEntryAdapter(key, entry)
    }

    companion object {
        /**
         * ID for the "disease agent targeted" value set.
         */
        const val DISEASE_AGENT_TARGETED_ID = "disease-agent-targeted"

        /**
         * ID for "Covid-19 lab test manufacturer (and name)" value set.
         */
        const val TEST_MANUFACTURER_AND_NAME_ID = "covid-19-lab-test-manufacturer-and-name"

        /**
         * ID for "Covid-19 lab result" value set.
         */
        const val LAB_RESULT_ID = "covid-19-lab-result"

        /**
         * ID for "Covid-19 lab test type" value set.
         */
        const val TEST_TYPE_ID = "covid-19-lab-test-type"

        /**
         * ID for "Covid-19 marketing authorization holders" value set.
         */
        const val MARKETING_AUTH_HOLDERS_ID = "vaccines-covid-19-auth-holders"

        /**
         * ID for "Vaccine medical product" value set.
         */
        const val MEDICAL_PRODUCT_ID = "vaccines-covid-19-names"

        /**
         * ID for "Vaccine Prophylaxis" value set.
         */
        const val VACCINE_PROPHYLAXIS_ID = "sct-vaccines-covid-19"

        /**
         * ID for the country code value set.
         */
        const val COUNTRY_CODES_ID = "country-2-codes"

        private val jsonDeserializer = Json { ignoreUnknownKeys = true }

        val INSTANCE: ValueSetHolder by lazy {
            val inputPaths = listOf(
                "/value-sets/country-2-codes.json",
                "/value-sets/disease-agent-targeted.json",
                "/value-sets/test-manf.json",
                "/value-sets/test-result.json",
                "/value-sets/test-type.json",
                "/value-sets/vaccine-mah-manf.json",
                "/value-sets/vaccine-medicinal-product.json",
                "/value-sets/vaccine-prophylaxis.json",
            )
            ValueSetHolder(inputPaths.map {
                jsonDeserializer.decodeFromString(this::class.java.getResource(it).readText())
            })
        }

        suspend fun getValueSetHolder(valueSetsRepository: ValueSetsRepository): ValueSetHolder {
            val result = mutableListOf<ValueSet>()

            val repositoryValueSets = valueSetsRepository.getValueSets()
            repositoryValueSets.firstOrNull { it.valueSetId == ValueSetHolder.COUNTRY_CODES_ID }?.let {
                result.add(ValueSet(it.valueSetId, it.valueSetDate, jsonDeserializer.decodeFromString(it.valueSetValues.toString())))
            } ?: run {
                result.add(jsonDeserializer.decodeFromString(this::class.java.getResource("/value-sets/country-2-codes.json").readText()))
            }

            repositoryValueSets.firstOrNull { it.valueSetId == ValueSetHolder.DISEASE_AGENT_TARGETED_ID }?.let {
                result.add(ValueSet(it.valueSetId, it.valueSetDate, jsonDeserializer.decodeFromString(it.valueSetValues.toString())))
            } ?: run {
                result.add(jsonDeserializer.decodeFromString(this::class.java.getResource("/value-sets/disease-agent-targeted.json").readText()))
            }

            repositoryValueSets.firstOrNull { it.valueSetId == ValueSetHolder.TEST_MANUFACTURER_AND_NAME_ID }?.let {
                result.add(ValueSet(it.valueSetId, it.valueSetDate, jsonDeserializer.decodeFromString(it.valueSetValues.toString())))
            } ?: run {
                result.add(jsonDeserializer.decodeFromString(this::class.java.getResource("/value-sets/test-manf.json").readText()))
            }

            repositoryValueSets.firstOrNull { it.valueSetId == ValueSetHolder.LAB_RESULT_ID }?.let {
                result.add(ValueSet(it.valueSetId, it.valueSetDate, jsonDeserializer.decodeFromString(it.valueSetValues.toString())))
            } ?: run {
                result.add(jsonDeserializer.decodeFromString(this::class.java.getResource("/value-sets/test-result.json").readText()))
            }

            repositoryValueSets.firstOrNull { it.valueSetId == ValueSetHolder.TEST_TYPE_ID }?.let {
                result.add(ValueSet(it.valueSetId, it.valueSetDate, jsonDeserializer.decodeFromString(it.valueSetValues.toString())))
            } ?: run {
                result.add(jsonDeserializer.decodeFromString(this::class.java.getResource("/value-sets/test-type.json").readText()))
            }

            repositoryValueSets.firstOrNull { it.valueSetId == ValueSetHolder.MARKETING_AUTH_HOLDERS_ID }?.let {
                result.add(ValueSet(it.valueSetId, it.valueSetDate, jsonDeserializer.decodeFromString(it.valueSetValues.toString())))
            } ?: run {
                result.add(jsonDeserializer.decodeFromString(this::class.java.getResource("/value-sets/vaccine-mah-manf.json").readText()))
            }

            repositoryValueSets.firstOrNull { it.valueSetId == ValueSetHolder.MEDICAL_PRODUCT_ID }?.let {
                result.add(ValueSet(it.valueSetId, it.valueSetDate, jsonDeserializer.decodeFromString(it.valueSetValues.toString())))
            } ?: run {
                result.add(jsonDeserializer.decodeFromString(this::class.java.getResource("/value-sets/vaccine-medicinal-product.json").readText()))
            }

            repositoryValueSets.firstOrNull { it.valueSetId == ValueSetHolder.VACCINE_PROPHYLAXIS_ID }?.let {
                result.add(ValueSet(it.valueSetId, it.valueSetDate, jsonDeserializer.decodeFromString(it.valueSetValues.toString())))
            } ?: run {
                result.add(jsonDeserializer.decodeFromString(this::class.java.getResource("/value-sets/vaccine-prophylaxis.json").readText()))
            }
            return ValueSetHolder(result)
        }
    }
}

@Serializable
data class ValueSet(
    val valueSetId: String,
    @Serializable(with = LocalDateSerializer::class)
    val valueSetDate: LocalDate,
    val valueSetValues: Map<String, ValueSetEntry>
)

@Serializable
data class ValueSetEntry(
    val display: String,
    val lang: String,
    val active: Boolean,
    val system: String,
    val version: String,
    val valueSetId: String? = null
)

@Serializable(with = ValueSetEntryAdapterSerializer::class)
data class ValueSetEntryAdapter(
    val key: String,
    val valueSetEntry: ValueSetEntry
)