package ai.comake.petping.ui.etc.notice

import ai.comake.petping.R
import ai.comake.petping.data.vo.NoticeResponseData
import ai.comake.petping.databinding.ItemNoticeBinding
import ai.comake.petping.ui.base.BindingViewHolder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

/**
 * android-petping-2
 * Class: NoticeAdapter
 * Created by cliff on 2022/02/22.
 *
 * Description:
 */
class NoticeAdapter(
    private val viewModel: NoticeViewModel
): ListAdapter<NoticeResponseData, NoticeAdapter.NoticeItemViewHolder>(TaskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        NoticeItemViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_notice,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: NoticeItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class NoticeItemViewHolder(view: View) : BindingViewHolder<ItemNoticeBinding>(view) {
        fun bind(item: NoticeResponseData) {
            binding.item = item
            binding.viewmodel = viewModel
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