package ai.comake.petping.ui.etc.inquiry

import ai.comake.petping.R
import ai.comake.petping.data.vo.InquiryData
import ai.comake.petping.databinding.AdapterInquiryItemBinding
import ai.comake.petping.ui.base.BindingViewHolder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

/**
 * android-petping-2
 * Class: InquiryDoAdapter
 * Created by cliff on 2022/02/22.
 *
 * Description:
 */
class InquiryDoAdapter(
    private val viewModel: InquiryDoViewModel
) : ListAdapter<InquiryData, InquiryDoAdapter.ViewHolder>(TaskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.adapter_inquiry_item,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(view: View) : BindingViewHolder<AdapterInquiryItemBinding>(view) {
        fun bind(item: InquiryData) {
            binding.viewModel = viewModel
            binding.item = item
        }
    }

    class TaskDiffCallback : DiffUtil.ItemCallback<InquiryData>() {
        override fun areItemsTheSame(oldItem: InquiryData, newItem: InquiryData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: InquiryData, newItem: InquiryData): Boolean {
            return oldItem == newItem
        }
    }
}