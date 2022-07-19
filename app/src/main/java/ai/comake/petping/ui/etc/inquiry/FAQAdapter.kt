package ai.comake.petping.ui.etc.inquiry

import ai.comake.petping.R
import ai.comake.petping.data.vo.NoticeResponseData
import ai.comake.petping.databinding.AdapterFaqItemBinding
import ai.comake.petping.ui.base.BindingViewHolder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

/**
 * android-petping-2
 * Class: FAQAdapter
 * Created by cliff on 2022/02/22.
 *
 * Description:
 */
class FAQAdapter(
    private val viewModel:FAQViewModel
): ListAdapter<NoticeResponseData, FAQAdapter.ViewHolder>(TaskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.adapter_faq_item,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(view: View)
        : BindingViewHolder<AdapterFaqItemBinding>(view) {
        fun bind(item: NoticeResponseData) {
            binding.viewModel = viewModel
            binding.item = item
        }
    }

    class TaskDiffCallback : DiffUtil.ItemCallback<NoticeResponseData>() {
        override fun areItemsTheSame(oldItem: NoticeResponseData, newItem: NoticeResponseData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: NoticeResponseData, newItem: NoticeResponseData): Boolean {
            return oldItem == newItem
        }
    }
}