package ro.sts.dgc.ui.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ro.sts.dgc.databinding.ItemRecoveryBinding
import ro.sts.dgc.ui.model.RecoveryModel

class RecoveryViewHolder(private val binding: ItemRecoveryBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(data: RecoveryModel) {
        binding.targetValue.text = data.target.valueSetEntry.display
        binding.dateOfFirstPositiveValue.text = data.dateOfFirstPositiveTestResult.toString()
        binding.countryValue.text = data.country.valueSetEntry.display
        binding.certificateIssuerValue.text = data.certificateIssuer
        binding.certificateValidFromValue.text = data.certificateValidFrom.toString()
        binding.certificateValidUntilValue.text = data.certificateValidUntil.toString()
    }

    companion object {
        fun create(inflater: LayoutInflater, parent: ViewGroup) =
            RecoveryViewHolder(ItemRecoveryBinding.inflate(inflater, parent, false))
    }
}