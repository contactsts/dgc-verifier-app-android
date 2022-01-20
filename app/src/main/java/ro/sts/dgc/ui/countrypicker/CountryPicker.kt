package ro.sts.dgc.ui.countrypicker

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.emoji.text.EmojiCompat
import ro.sts.dgc.R

class CountryPicker @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {
    private val country: TextView
    private val arrow: ImageView

    internal val attrs: CountryPickerAttrs

    private val emoji: EmojiCompat?

    var countries: List<Country> = emptyList()
    var selectedCountry: Country
    var onSelectedCountryChanged: ((Country) -> Unit)? = null

    private var clickListener: OnClickListener? = null

    private var dialog: CountryPickerDialog? = null

    private val internalClickListener = OnClickListener {
        if (isClickable) {
            clickListener?.onClick(it) ?: run {
                dialog?.close()

                dialog = CountryPickerDialog(this, countries)
            }
        }
    }

    init {
        if (childCount == 0) {
            val layout = if (attrs.isWidthMatchParent()) {
                R.layout.country_picker_full_width
            } else {
                R.layout.country_picker
            }
            LayoutInflater.from(context).inflate(layout, this, true)
        }

        super.setOnClickListener(internalClickListener)

        isClickable = attrs.isClickable()

        descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS

        setPadding(pxToDp(8))

        country = findViewById(R.id.country)
        arrow = findViewById(R.id.arrow)

        this.attrs = CountryPickerAttrs.fromAttrs(context, attrs)

        if (!this.attrs.showFlag && !this.attrs.showCountryCode) {
            throw IllegalArgumentException("Can't show nothing")
        }

        arrow.isVisible = this.attrs.showArrow

        countries = Country.countries.buildCountryList(this.attrs)

        emoji = if (this.attrs.useEmojiCompat) {
            EmojiCompat.get()
        } else {
            null
        }

        selectedCountry = this.attrs.defaultCountry
        renderSelectedCountry()
    }

    fun setAllowedCountries(dialogAllowedCountries: List<String>) {
        countries = Country.countries.buildCountryList(this.attrs, dialogAllowedCountries)
    }

    fun setCountry(country: Country){
        selectedCountry = country
        renderSelectedCountry()
    }

    override fun setOnClickListener(listener: OnClickListener?) {
        clickListener = listener
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        dialog = dialog?.let { it.close(); null }
    }

    internal fun onCountrySelected(selectedCountry: Country) {
        dialog = dialog?.let { it.close(); null }
        this.selectedCountry = selectedCountry
        onSelectedCountryChanged?.invoke(selectedCountry)
        renderSelectedCountry()
    }

    private fun renderSelectedCountry() {
        with(selectedCountry) {
            with(StringBuilder()) {
                if (attrs.showFlag) {
                    val flagToDisplay = if (attrs.useEmojiCompat && emoji != null) {
                        try {
                            emoji.process(flag)
                        } catch (_: Throwable) {
                            flag
                        }
                    } else {
                        flag
                    }

                    append(flagToDisplay)
                }

                if (attrs.showCountryCode) {
                    if (isNotEmpty()) {
                        append(" ")
                    }
                    append(context.getString(name))
                }

                country.text = "$this"
            }
        }
    }

    private class SavedState : BaseSavedState {
        val country: Country

        constructor(
            parcelable: Parcelable?,
            country: Country
        ) : super(parcelable) {
            this.country = country
        }

        private constructor(source: Parcel) : super(source) {
            country = Country(
                countryCode = source.readString()!!,
                callingCode = source.readString()!!,
                name = source.readInt(),
                flag = source.readString()!!
            )
        }

        override fun writeToParcel(out: Parcel?, flags: Int) {
            super.writeToParcel(out, flags)
            out?.apply {
                writeString(country.countryCode)
                writeString(country.callingCode)
                writeInt(country.name)
                writeString(country.flag)
            }
        }

        companion object {
            @Suppress("unused")
            @JvmField
            val CREATOR = object : Parcelable.Creator<SavedState> {
                override fun createFromParcel(source: Parcel): SavedState = SavedState(source)

                override fun newArray(size: Int): Array<SavedState?> = arrayOfNulls(size)
            }
        }
    }

    override fun onSaveInstanceState(): Parcelable = SavedState(super.onSaveInstanceState(), selectedCountry)

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is SavedState) {
            super.onRestoreInstanceState(state.superState)
            selectedCountry = state.country
            renderSelectedCountry()
        } else {
            super.onRestoreInstanceState(state)
        }
    }
}

private fun AttributeSet?.isWidthMatchParent() =
    this?.run {
        getAttributeValue("http://schemas.android.com/apk/res/android", "layout_width")?.let { xmlWidth ->
            "match_parent" == xmlWidth || "fill_parent" == xmlWidth || Integer.valueOf(xmlWidth) == ConstraintLayout.LayoutParams.MATCH_PARENT
        }
    } ?: false

private fun AttributeSet?.isClickable() =
    this?.run {
        getAttributeValue("http://schemas.android.com/apk/res/android", "isClickable")?.toBoolean()
    } ?: true

private fun View.pxToDp(value: Int) =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value.toFloat(), resources.displayMetrics).toInt()

private fun Map<String, Country>.buildCountryList(attributes: CountryPickerAttrs) = with(toMutableMap()) {
    when {
        attributes.dialogIncludeCountries.isNotEmpty() -> keys.retainAll(attributes.dialogIncludeCountries)
        attributes.dialogExcludeCountries.isNotEmpty() -> keys.removeAll(attributes.dialogExcludeCountries)
    }

    if (attributes.dialogPriorityCountries.isEmpty()) {
        values.toList()
    } else {
        val reversedPriorityCountries = attributes.dialogPriorityCountries.mapNotNull(::remove).asReversed()
        val list = values.toMutableList().asReversed()

        reversedPriorityCountries
            .forEach {
                list.add(it.copy(priority = true))
            }

        list.asReversed()
    }
}

private fun Map<String, Country>.buildCountryList(attributes: CountryPickerAttrs, dialogAllowedCountries: List<String>) = with(toMutableMap()) {
    keys.removeIf {
        !dialogAllowedCountries.contains(it)
    }

    if (attributes.dialogPriorityCountries.isEmpty()) {
        values.toList()
    } else {
        val reversedPriorityCountries = attributes.dialogPriorityCountries.mapNotNull(::remove).asReversed()
        val list = values.toMutableList().asReversed()

        reversedPriorityCountries
            .forEach {
                list.add(it.copy(priority = true))
            }

        list.asReversed()
    }
}