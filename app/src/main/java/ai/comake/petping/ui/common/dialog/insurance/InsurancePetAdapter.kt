package ai.comake.petping.ui.common.dialog.insurance

import ai.comake.petping.data.vo.InsurancePet
import ai.comake.petping.databinding.ItemInsurancePetBinding
import ai.comake.petping.ui.base.BaseViewHolder
import ai.comake.petping.util.setSafeOnClickListener
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions

/**
 * android-petping-2
 * Class: InsurancePetAdapter
 * Created by cliff on 2022/03/22.
 *
 * Description:
 */
class InsurancePetAdapter(
    private val data: List<InsurancePet>,
    private val listener: (petId: Int) -> Unit
) : RecyclerView.Adapter<BaseViewHolder>() {

    private var selectedPosition = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding =
            ItemInsurancePetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PetItemViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    inner class PetItemViewHolder(binding: ItemInsurancePetBinding) : BaseViewHolder(binding.root) {
        private var mBinding: ItemInsurancePetBinding = binding

        override fun onBind(position: Int) {
            mBinding.executePendingBindings()
            mBinding.name.text = data[position].petName

            Glide.with(mBinding.profileImage.context)
                .load(data[position].profileImageURL)
                .apply(RequestOptions.circleCropTransform())
                .into(mBinding.profileImage)

            mBinding.itemRoot.setSafeOnClickListener {
                if (!mBinding.selRadio.isSelected) {
                    val prevPosition = selectedPosition
                    selectedPosition = position
                    mBinding.selRadio.isSelected = true
                    notifyItemChanged(prevPosition)
                    listener.invoke(data[position].petId)
                }
            }
            mBinding.selRadio.isSelected = selectedPosition == position
        }
    }
}