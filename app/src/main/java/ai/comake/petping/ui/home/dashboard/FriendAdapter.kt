package ai.comake.petping.ui.home.dashboard

import ai.comake.petping.R
import ai.comake.petping.data.vo.PingZoneMeetPet
import ai.comake.petping.databinding.ItemFriendViewBinding
import ai.comake.petping.ui.base.BindingViewHolder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

/**
 * android-petping-2
 * Class: FriendAdapter
 * Created by cliff on 2022/08/25.
 *
 * Description:
 */
class FriendAdapter(
    private val viewModel: DashboardViewModel
): ListAdapter<PingZoneMeetPet, FriendAdapter.ItemViewHolder>(TaskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ItemViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_friend_view,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ItemViewHolder(view: View) : BindingViewHolder<ItemFriendViewBinding>(view) {
        fun bind(item: PingZoneMeetPet) {
            binding.item = item
            binding.viewModel = viewModel
        }
    }

    class TaskDiffCallback : DiffUtil.ItemCallback<PingZoneMeetPet>() {
        override fun areItemsTheSame(oldItem: PingZoneMeetPet, newItem: PingZoneMeetPet): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PingZoneMeetPet, newItem: PingZoneMeetPet): Boolean {
            return oldItem == newItem
        }
    }
}