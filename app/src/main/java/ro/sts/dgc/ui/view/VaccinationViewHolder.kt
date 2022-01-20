package ro.sts.dgc.ui.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ro.sts.dgc.databinding.ItemVaccinationBinding
import ro.sts.dgc.ui.model.VaccinationModel

class VaccinationViewHolder(private val binding: ItemVaccinationBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(data: VaccinationModel) {
        binding.targetValue.text = data.target.valueSetEntry.display
        binding.vaccineValue.text = data.vaccine.valueSetEntry.display
        binding.vaccineMedicinalProductValue.text = data.medicinalProduct.valueSetEntry.display
        binding.vaccineMarketingAuthorizationValue.text = data.authorizationHolder.valueSetEntry.display
        binding.dateValue.text = data.date.toString()
        binding.vaccineNumberOverallDosesValue.text = data.doseNumber.toString() + " / " + data.doseTotalNumber.toString()
        binding.countryValue.text = data.country.valueSetEntry.display
        binding.certificateIssuerValue.text = data.certificateIssuer
    }

    companion object {
        fun create(inflater: LayoutInflater, parent: ViewGroup) = VaccinationViewHolder(ItemVaccinationBinding.inflate(inflater, parent, false))
    }
}