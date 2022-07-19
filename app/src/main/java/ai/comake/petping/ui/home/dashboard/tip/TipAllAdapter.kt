package ai.comake.petping.ui.home.dashboard.tip

import ai.comake.petping.R
import ai.comake.petping.data.vo.Tip
import ai.comake.petping.databinding.ItemPingTipAllBinding
import ai.comake.petping.ui.base.BindingViewHolder
import ai.comake.petping.ui.home.dashboard.DashboardViewModel
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

/**
 * android-petping-2
 * Class: TipAllAdapter
 * Created by cliff on 2022/05/20.
 *
 * Description:
 */
class TipAllAdapter(
    private val viewModel: TipAllViewModel
): ListAdapter<Tip, TipAllAdapter.ViewHolder>(TaskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_ping_tip_all,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(view: View) : BindingViewHolder<ItemPingTipAllBinding>(view) {
        fun bind(item: Tip) {
            binding.item = item
            binding.viewModel = viewModel
        }
    }

    class TaskDiffCallback : DiffUtil.ItemCallback<Tip>() {
        override fun areItemsTheSame(oldItem: Tip, newItem: Tip): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Tip, newItem: Tip): Boolean {
            return oldItem == newItem
        }
    }
}