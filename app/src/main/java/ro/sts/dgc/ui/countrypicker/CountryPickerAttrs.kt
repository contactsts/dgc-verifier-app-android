package ro.sts.dgc.ui.countrypicker

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.res.use
import androidx.core.os.ConfigurationCompat
import ro.sts.dgc.R
import java.util.*

internal data class CountryPickerAttrs(
    val defaultCountry: Country,
    val useEmojiCompat: Boolean,
    val showFlag: Boolean,
    val showCountryCode: Boolean,
    val showArrow: Boolean,
    val dialogShowTitle: Boolean,
    val dialogTitle: String,
    val dialogShowSearch: Boolean,
    val dialogSearchHint: String,
    val dialogEmptyViewText: String,
    val dialogPriorityCountries: Set<String>,
    val dialogIncludeCountries: Set<String>,
    val dialogExcludeCountries: Set<String>,
    val dialogCountryTextAppearance: Int?,
    val dialogCountryCodeTextAppearance: Int?,
) {
    companion object {
        fun fromAttrs(context: Context, attrs: AttributeSet?) = with(context) {
            attrs?.let { attributes ->
                theme.obtainStyledAttributes(attributes, R.styleable.CountryPicker, 0, 0).use { a ->
                    CountryPickerAttrs(
                        defaultCountry = (a.getString(R.styleable.CountryPicker_cp_default_country)
                            ?.lowercase(Locale.ROOT)
                            ?.takeIf { it in Country.countries }
                            ?: defaultCountry)
                            .let { countryCode ->
                                Country.countries.getValue(countryCode)
                            },

                        useEmojiCompat = a.getBoolean(R.styleable.CountryPicker_cp_use_emoji_compat, false),

                        showFlag = a.getBoolean(R.styleable.CountryPicker_cp_show_flag, true),

                        showCountryCode = a.getBoolean(R.styleable.CountryPicker_cp_show_country_code, true),

                        showArrow = a.getBoolean(R.styleable.CountryPicker_cp_show_arrow, true),

                        dialogShowTitle = a.getBoolean(R.styleable.CountryPicker_cp_dialog_show_title, true),

                        dialogTitle = a.getString(R.styleable.CountryPicker_cp_dialog_title) ?: context.defaultDialogTitle,

                        dialogShowSearch = a.getBoolean(R.styleable.CountryPicker_cp_dialog_show_search, true),

                        dialogSearchHint = a.getString(R.styleable.CountryPicker_cp_dialog_search_hint) ?: context.defaultDialogSearchHint,

                        dialogEmptyViewText = a.getString(R.styleable.CountryPicker_cp_dialog_empty_view_text) ?: context.defaultDialogEmptyViewText,

                        dialogPriorityCountries = a.getString(R.styleable.CountryPicker_cp_dialog_priority_countries).csvToSet(),

                        dialogIncludeCountries = a.getString(R.styleable.CountryPicker_cp_dialog_include_countries).csvToSet(),

                        dialogExcludeCountries = a.getString(R.styleable.CountryPicker_cp_dialog_exclude_countries).csvToSet(),

                        dialogCountryTextAppearance = a.getResourceId(R.styleable.CountryPicker_cp_dialog_country_text_appearance, -1).takeIf { it != -1 },

                        dialogCountryCodeTextAppearance = a.getResourceId(R.styleable.CountryPicker_cp_dialog_country_code_text_appearance, -1).takeIf { it != -1 },
                    )
                }
            } ?: CountryPickerAttrs(
                defaultCountry = Country.countries.getValue(context.defaultCountry),
                useEmojiCompat = false,
                showFlag = true,
                showCountryCode = true,
                showArrow = true,
                dialogShowTitle = true,
                dialogTitle = context.defaultDialogTitle,
                dialogShowSearch = true,
                dialogSearchHint = context.defaultDialogSearchHint,
                dialogEmptyViewText = context.defaultDialogEmptyViewText,
                dialogPriorityCountries = emptySet(),
                dialogIncludeCountries = emptySet(),
                dialogExcludeCountries = emptySet(),
                dialogCountryTextAppearance = null,
                dialogCountryCodeTextAppearance = null,
            )
        }
    }
}

private val Context.defaultDialogTitle get() = getString(R.string.country_picker_dialog_default_title)
private val Context.defaultDialogSearchHint get() = getString(R.string.country_picker_dialog_default_search_hint)
private val Context.defaultDialogEmptyViewText get() = getString(R.string.country_picker_dialog_default_empty_view_text)

private val Context.defaultCountry
    get() = ConfigurationCompat
        .getLocales(resources.configuration)
        .get(0)
        .country
        .lowercase(Locale.ROOT)

private fun String?.csvToSet() =
    this
        ?.split(",")
        ?.map { it.lowercase(Locale.ROOT) }
        ?.filter { it in Country.countries }
        ?.toSet()
        ?: emptySet()