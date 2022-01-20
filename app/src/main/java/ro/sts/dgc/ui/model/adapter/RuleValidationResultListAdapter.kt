package ro.sts.dgc.ui.model.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ro.sts.dgc.R
import ro.sts.dgc.databinding.ItemRuleValidationResultBinding
import ro.sts.dgc.rules.Result
import ro.sts.dgc.ui.model.RuleValidationResultModel

class RuleValidationResultListAdapter(private val inflater: LayoutInflater) : RecyclerView.Adapter<RuleValidationResultListAdapter.RuleValidationResultViewHolder>() {

    private var items = emptyList<RuleValidationResultModel>()

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RuleValidationResultViewHolder = RuleValidationResultViewHolder.create(inflater, parent)

    override fun onBindViewHolder(holder: RuleValidationResultViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun update(list: List<RuleValidationResultModel>) {
        notifyChanges(items, list)
        items = list
    }

    private fun notifyChanges(oldList: List<RuleValidationResultModel>, newList: List<RuleValidationResultModel>) {
        val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldList[oldItemPosition].countryIsoCode == newList[newItemPosition].countryIsoCode
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldList[oldItemPosition] == newList[newItemPosition]
            }

            override fun getOldListSize() = oldList.size

            override fun getNewListSize() = newList.size

            override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any {
                return Bundle()
            }
        })

        diff.dispatchUpdatesTo(this)
    }

    class RuleValidationResultViewHolder(private val binding: ItemRuleValidationResultBinding) : RecyclerView.ViewHolder(binding.root) {
        private fun Result.getLocalizedText(context: Context): String = context.getString(
            when (this) {
                Result.PASSED -> R.string.passed
                Result.FAIL -> R.string.failed
                Result.OPEN -> R.string.open
            }
        )

        fun bind(ruleValidationResultModel: RuleValidationResultModel) {
            binding.container.setBackgroundResource(
                when (ruleValidationResultModel.result) {
                    Result.PASSED -> R.drawable.bg_rect_rounded_small_green
                    Result.OPEN -> R.drawable.bg_rect_rounded_small_grey
                    Result.FAIL -> R.drawable.bg_rect_rounded_small_red
                }
            )

            binding.ruleVerificationResultHeader.text = ruleValidationResultModel.result.getLocalizedText(itemView.context)
            binding.ruleVerificationResultHeader.setTextColor(
                ResourcesCompat.getColor(
                    itemView.resources,
                    when (ruleValidationResultModel.result) {
                        Result.PASSED -> R.color.green
                        Result.OPEN -> R.color.grey_50
                        Result.FAIL -> R.color.red
                    },
                    null
                )
            )
            binding.ruleVerificationResultHeader.setCompoundDrawablesWithIntrinsicBounds(
                when (ruleValidationResultModel.result) {
                    Result.PASSED -> R.drawable.ic_hcert_valid_small
                    Result.OPEN -> R.drawable.ic_hcert_open_small
                    Result.FAIL -> R.drawable.ic_hcert_invalid_small
                }, 0, 0, 0
            )

            binding.description.text = ruleValidationResultModel.description

            if (ruleValidationResultModel.current.isNotBlank()) {
                binding.current.text = ruleValidationResultModel.current
                View.VISIBLE
            } else {
                View.GONE
            }.apply {
                binding.ruleVerificationCurrentTitle.visibility = this
                binding.current.visibility = this
            }
        }

        companion object {
            fun create(inflater: LayoutInflater, parent: ViewGroup) = RuleValidationResultViewHolder(ItemRuleValidationResultBinding.inflate(inflater, parent, false))
        }
    }
}
