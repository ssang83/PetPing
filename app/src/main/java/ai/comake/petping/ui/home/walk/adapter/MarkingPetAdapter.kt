package ai.comake.petping.ui.home.walk.adapter

import ai.comake.petping.R
import ai.comake.petping.data.vo.WalkablePet
import ai.comake.petping.databinding.ItemMarkingPetBinding
import ai.comake.petping.ui.base.BaseViewHolder
import ai.comake.petping.util.setSafeOnClickListener
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class MarkingPetAdapter(
    var data: List<WalkablePet.Pets>,
    val listener: (Int) -> Unit
) :
    RecyclerView.Adapter<BaseViewHolder>() {
    private var selectedPosition = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding =
            ItemMarkingPetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MarkingPetItemViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    inner class MarkingPetItemViewHolder(binding: ItemMarkingPetBinding) :
        BaseViewHolder(binding.root) {
        private var mBinding: ItemMarkingPetBinding = binding
        override fun onBind(position: Int) {
            mBinding.name.text = data[position].name.let {
                if (it.length > 4) it.substring(0, 4)
                else it
            }

            Glide.with(mBinding.petImageView.context).load(data[position].profileImageURL).centerCrop()
                .into(mBinding.petImageView)

            if (position == selectedPosition) {
                mBinding.petView.background = mBinding.petView.context.getDrawable(R.drawable.sel_circle)
            } else {
                mBinding.petView.background = null
            }

            mBinding.petView.setSafeOnClickListener {
                val prevPosition = selectedPosition
                selectedPosition = position
                mBinding.petView.background = mBinding.petView.context.getDrawable(R.drawable.sel_circle)
                notifyItemChanged(prevPosition)
                listener(data[position].id)
            }
        }
    }
}