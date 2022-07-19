package ai.comake.petping.ui.home.shop

import ai.comake.petping.R
import ai.comake.petping.data.vo.RecommendGoods
import ai.comake.petping.databinding.AdapterShopItemBinding
import ai.comake.petping.ui.base.BindingViewHolder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

class ShopAdapter(
    private val viewModel: ShopViewModel
) : ListAdapter<RecommendGoods, ShopAdapter.ViewHolder>(TaskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.adapter_shop_item,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(view: View) : BindingViewHolder<AdapterShopItemBinding>(view) {
        fun bind(item: RecommendGoods) {
            binding.viewModel = viewModel
            binding.item = item
        }
    }

    class TaskDiffCallback : DiffUtil.ItemCallback<RecommendGoods>() {
        override fun areItemsTheSame(oldItem: RecommendGoods, newItem: RecommendGoods): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: RecommendGoods, newItem: RecommendGoods): Boolean {
            return oldItem == newItem
        }
    }
}