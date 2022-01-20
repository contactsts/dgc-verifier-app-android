package ro.sts.dgc.ui.countrypicker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.emoji.text.EmojiCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ro.sts.dgc.R
import ro.sts.dgc.stripAccents

internal class CountryAdapter(
    private val countryPicker: CountryPicker,
    private val countries: List<Country>,
    private val attrs: CountryPickerAttrs,
    private val onListChanged: (List<Country>) -> Unit
) : ListAdapter<Country, CountryViewHolder>(diffCallback) {
    init {
        submitList(countries)
    }

    private val emoji: EmojiCompat? = if (countryPicker.attrs.useEmojiCompat) EmojiCompat.get() else null

    private val clickListener = View.OnClickListener { view ->
        val position = view.getTag(R.id.cp_selected_country_position) as Int
        countryPicker.onCountrySelected(getItem(position))
    }

    override fun getItemId(position: Int): Long = getItem(position).name.toLong()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryViewHolder =
        CountryViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.country_picker_item, parent, false)).apply {
            attrs.dialogCountryTextAppearance?.let { country.setTextAppearance(it) }
            attrs.dialogCountryCodeTextAppearance?.let { code.setTextAppearance(it) }
        }

    override fun onBindViewHolder(holder: CountryViewHolder, position: Int) {
        holder.itemView.setTag(R.id.cp_selected_country_position, position)
        holder.itemView.setOnClickListener(clickListener)

        val country = getItem(position)
        val name = holder.itemView.context.getString(country.name)
        val flag = try {
            emoji?.process(country.flag) ?: country.flag
        } catch (_: Throwable) {
            country.flag
        }

        holder.country.text = "$flag $name"
        holder.code.text = "${country.countryCode}"
        holder.divider.isVisible = position != itemCount - 1
    }

    fun updateSearch(search: CharSequence) {
        if (search.isBlank()) {
            submitList(countries)
            return
        }

        val normalizedSearch = search.stripAccents()

        val filteredList = countries
            .filter { country ->
                val name = countryPicker.context.getString(country.name).stripAccents()
                name.startsWith(normalizedSearch, ignoreCase = true) || name.split(" ").any {
                    it.startsWith(normalizedSearch, ignoreCase = true)
                }
            }

        submitList(filteredList)
    }

    override fun onCurrentListChanged(previousList: MutableList<Country>, currentList: MutableList<Country>) {
        super.onCurrentListChanged(previousList, currentList)

        onListChanged(currentList)
    }

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<Country>() {
            override fun areItemsTheSame(oldItem: Country, newItem: Country) = oldItem == newItem

            override fun areContentsTheSame(oldItem: Country, newItem: Country) = false
        }
    }
}

internal class CountryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val country: TextView = itemView.findViewById(R.id.country)
    val code: TextView = itemView.findViewById(R.id.code)
    val divider: View = itemView.findViewById(R.id.divider)
}