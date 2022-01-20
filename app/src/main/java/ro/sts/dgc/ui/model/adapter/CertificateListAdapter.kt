package ro.sts.dgc.ui.model.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ro.sts.dgc.model.ContentType
import ro.sts.dgc.ui.model.CertificateData
import ro.sts.dgc.ui.model.RecoveryModel
import ro.sts.dgc.ui.model.TestModel
import ro.sts.dgc.ui.model.VaccinationModel
import ro.sts.dgc.ui.view.RecoveryViewHolder
import ro.sts.dgc.ui.view.TestViewHolder
import ro.sts.dgc.ui.view.VaccinationViewHolder

class CertificateListAdapter(private val inflater: LayoutInflater) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items = emptyList<CertificateData>()

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ContentType.VACCINATION.ordinal -> VaccinationViewHolder.create(inflater, parent)
            ContentType.TEST.ordinal -> TestViewHolder.create(inflater, parent)
            ContentType.RECOVERY.ordinal -> RecoveryViewHolder.create(inflater, parent)
            else -> throw IllegalArgumentException("View type not defined")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = items[position]
        when (holder) {
            is VaccinationViewHolder -> holder.bind(data as VaccinationModel)
            is TestViewHolder -> holder.bind(data as TestModel)
            is RecoveryViewHolder -> holder.bind(data as RecoveryModel)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is VaccinationModel -> ContentType.VACCINATION.ordinal
            is TestModel -> ContentType.TEST.ordinal
            is RecoveryModel -> ContentType.RECOVERY.ordinal
            else -> throw IllegalStateException("Type not supported")
        }
    }

    fun update(list: List<CertificateData>) {
        notifyChanges(items, list)
        items = list
    }

    private fun notifyChanges(oldList: List<CertificateData>, newList: List<CertificateData>) {
        val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldList[oldItemPosition].target == newList[newItemPosition].target
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
}