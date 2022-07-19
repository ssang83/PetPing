package ai.comake.petping.ui.common.dialog.reward

import ai.comake.petping.databinding.ItemRewardImageBinding
import ai.comake.petping.ui.base.BaseViewHolder
import ai.comake.petping.util.setSafeOnClickListener
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

/**
 * android-petping-2
 * Class: RewardImageAdapter
 * Created by cliff on 2022/02/15.
 *
 * Description:
 */
class RewardImageAdapter(
    val removeImage: (position: Int) -> Unit
) : RecyclerView.Adapter<BaseViewHolder>() {

    var mItems = mutableListOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding =
            ItemRewardImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RewardImageViewHolder(binding)
    }

    override fun getItemCount() = mItems.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    fun updateData(items: List<String>) {
        this.mItems.clear()
        this.mItems.addAll(items)
        notifyDataSetChanged()
    }

    inner class RewardImageViewHolder(
        binding: ItemRewardImageBinding
    ) : BaseViewHolder(binding.root) {
        private var mBinding:ItemRewardImageBinding = binding

        override fun onBind(position: Int) {
            mBinding.executePendingBindings()
            Glide.with(mBinding.imageView.context).load(mItems[position])
                .centerCrop().into(mBinding.imageView)

            mBinding.delImgBtn.setSafeOnClickListener {
                removeImage.invoke(position)
            }
        }
    }
}