package ai.comake.petping.ui.history.walk

import ai.comake.petping.R
import ai.comake.petping.databinding.WalkHistoryImageItemBinding
import ai.comake.petping.ui.base.BindingViewHolder
import ai.comake.petping.ui.home.shop.ShopAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * android-petping-2
 * Class: ImagePagerAdapter
 * Created by cliff on 2022/02/25.
 *
 * Description:
 */
class ImagePagerAdapter(
    private val viewModel: WalkHistoryViewModel,
    private val imageList: List<String>
) : RecyclerView.Adapter<ImagePagerAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ImageViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.walk_history_image_item,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(imageList[position])
    }

    override fun getItemCount() = imageList.size


    inner class ImageViewHolder(view: View)
        : BindingViewHolder<WalkHistoryImageItemBinding>(view) {
        fun bind(url: String) {
            binding.viewModel = viewModel
            binding.url = url
        }
    }
}