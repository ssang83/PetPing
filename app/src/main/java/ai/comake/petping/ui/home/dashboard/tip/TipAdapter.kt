package ai.comake.petping.ui.home.dashboard.tip

import ai.comake.petping.R
import ai.comake.petping.data.vo.Tip
import ai.comake.petping.databinding.ItemPingTipBinding
import ai.comake.petping.ui.base.BindingViewHolder
import ai.comake.petping.ui.home.dashboard.DashboardViewModel
import ai.comake.petping.util.dpToPixels
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

/**
 * android-petping-2
 * Class: TipAdapter
 * Created by cliff on 2022/05/20.
 *
 * Description:
 */
class TipAdapter(
    val context: Context,
    private val viewModel: DashboardViewModel
): ListAdapter<Tip, TipAdapter.ViewHolder>(TaskDiffCallback()) {

    private var margin15 = 0f
    private var margin2 = 0f

    init {
        margin15 = 20.dpToPixels(context)
        margin2 = 2.dpToPixels(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_ping_tip,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position == 0) {
            (holder.binding.root.layoutParams as RecyclerView.LayoutParams).apply {
                leftMargin = margin15.toInt()
                rightMargin = margin2.toInt()
            }
        } else if (position == itemCount - 1) {
            (holder.binding.root.layoutParams as RecyclerView.LayoutParams).apply {
                rightMargin = margin15.toInt()
                leftMargin = margin2.toInt()
            }
        } else {
            (holder.binding.root.layoutParams as RecyclerView.LayoutParams).apply {
                leftMargin = margin2.toInt()
                rightMargin = margin2.toInt()
            }
        }

        holder.bind(getItem(position))
    }

    inner class ViewHolder(view: View) : BindingViewHolder<ItemPingTipBinding>(view) {
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