package ai.comake.petping.ui.home.walk.adapter

import ai.comake.petping.data.vo.MarkingPoi
import ai.comake.petping.databinding.FragmentWalkClusterDetailListItemBinding
import ai.comake.petping.ui.home.walk.WalkViewModel
import ai.comake.petping.util.LogUtil
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class MarkingDetailClusterRecyclerViewAdapter(
    val viewModel: WalkViewModel
) :
    ListAdapter<MarkingPoi.Pois, MarkingDetailClusterRecyclerViewAdapter.ViewHolder>(TaskDiffCallback()) {

    inner class ViewHolder(
        private val binding: FragmentWalkClusterDetailListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(viewModel: WalkViewModel, item: MarkingPoi.Pois) {
            binding.viewModel = viewModel
            binding.data = item
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            FragmentWalkClusterDetailListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(viewModel, getItem(position))
    }

    class TaskDiffCallback : DiffUtil.ItemCallback<MarkingPoi.Pois>() {
        override fun areItemsTheSame(oldItem: MarkingPoi.Pois, newItem: MarkingPoi.Pois): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MarkingPoi.Pois, newItem: MarkingPoi.Pois): Boolean {
            return oldItem == newItem
        }
    }
}


