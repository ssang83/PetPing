package ai.comake.petping.ui.history.walk

import ai.comake.petping.R
import ai.comake.petping.data.vo.WalkRecord
import ai.comake.petping.databinding.WalkHistoryItemBinding
import ai.comake.petping.ui.base.BindingViewHolder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

/**
 * android-petping-2
 * Class: WalkHistoryAdapter
 * Created by cliff on 2022/02/25.
 *
 * Description:
 */
class WalkHistoryAdapter(
    private val viewModel: WalkHistoryViewModel
) : PagingDataAdapter<WalkRecord, WalkHistoryAdapter.HistoryViewHolder>(DATA_COMPARETOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        HistoryViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.walk_history_item,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        getItem(position)?.let { item ->
            holder.bind(item)
        }
    }

    inner class HistoryViewHolder(view: View) : BindingViewHolder<WalkHistoryItemBinding>(view) {
        fun bind(item: WalkRecord) {
            binding.viewModel = viewModel
            binding.item = item
        }
    }

    companion object {
        private val DATA_COMPARETOR = object : DiffUtil.ItemCallback<WalkRecord>() {
            override fun areItemsTheSame(oldItem: WalkRecord, newItem: WalkRecord): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: WalkRecord, newItem: WalkRecord): Boolean =
                oldItem == newItem
        }
    }
}