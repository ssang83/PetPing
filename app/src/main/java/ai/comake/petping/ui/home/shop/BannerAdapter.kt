package ai.comake.petping.ui.home.shop

import ai.comake.petping.R
import ai.comake.petping.data.vo.ShopPopup
import ai.comake.petping.databinding.ItemShopBannerBinding
import ai.comake.petping.ui.base.BindingViewHolder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * android-petping-2
 * Class: BannerAdapter
 * Created by cliff on 2022/08/02.
 *
 * Description:
 */
class BannerAdapter(
    private val viewModel: ShopViewModel,
    bannerItems: List<ShopPopup>
) : RecyclerView.Adapter<BannerAdapter.ViewHolder>() {

    private var bannerList = bannerItems

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_shop_banner,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(bannerList[position % bannerList.size])
    }

    override fun getItemCount() = if (bannerList.size > 1) {
        Int.MAX_VALUE
    } else {
        bannerList.size
    }

    inner class ViewHolder(view: View) : BindingViewHolder<ItemShopBannerBinding>(view) {
        fun bind(item : ShopPopup) {
            binding.viewModel = viewModel
            binding.item = item
        }
    }

}