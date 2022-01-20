package ro.sts.dgc.ui.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ro.sts.dgc.databinding.ItemTestBinding
import ro.sts.dgc.toFormattedDateTime
import ro.sts.dgc.ui.model.TestModel

class TestViewHolder(private val binding: ItemTestBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(data: TestModel) {
        binding.targetValue.text = data.target.valueSetEntry.display
        binding.typeValue.text = data.type.valueSetEntry.display
        binding.testNameValue.text = data.nameNaa.orEmpty()
        binding.manufacturerValue.text = data.nameRat?.valueSetEntry?.display.orEmpty()
        binding.dateTimeSampleValue.text = data.dateTimeSample.toString().toFormattedDateTime()
        binding.resultPositiveValue.text = data.resultPositive.valueSetEntry.display
        binding.testingCentreValue.text = data.testFacility.orEmpty()
        binding.countryValue.text = data.country.valueSetEntry.display
        binding.certificateIssuerValue.text = data.certificateIssuer
    }

    companion object {
        fun create(inflater: LayoutInflater, parent: ViewGroup) =
            TestViewHolder(ItemTestBinding.inflate(inflater, parent, false))
    }
}