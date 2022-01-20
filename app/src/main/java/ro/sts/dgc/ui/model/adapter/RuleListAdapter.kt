package ro.sts.dgc.ui.model.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ro.sts.dgc.R
import ro.sts.dgc.databinding.ItemRuleBinding
import ro.sts.dgc.ui.model.RuleModel

class RuleListAdapter(private val inflater: LayoutInflater) : RecyclerView.Adapter<RuleListAdapter.RuleViewHolder>() {

    private var items = emptyList<RuleModel>()

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RuleListAdapter.RuleViewHolder = RuleListAdapter.RuleViewHolder.create(inflater, parent)

    override fun onBindViewHolder(holder: RuleListAdapter.RuleViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun update(list: List<RuleModel>) {
        notifyChanges(items, list)
        items = list
    }

    private fun notifyChanges(oldList: List<RuleModel>, newList: List<RuleModel>) {
        val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldList[oldItemPosition].identifier == newList[newItemPosition].identifier
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

    class RuleViewHolder(private val binding: ItemRuleBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(ruleModel: RuleModel) {
            binding.container.setBackgroundResource(R.drawable.bg_rect_rounded_small_grey)

            binding.identifier.text = ruleModel.identifier
            binding.certificateType.text = ruleModel.ruleCertificateType
            binding.description.text = ruleModel.description
            binding.validFrom.text = ruleModel.validFrom.toString()
            binding.validTo.text = ruleModel.validTo.toString()
        }

        companion object {
            fun create(inflater: LayoutInflater, parent: ViewGroup) = RuleViewHolder(ItemRuleBinding.inflate(inflater, parent, false))
        }
    }
}