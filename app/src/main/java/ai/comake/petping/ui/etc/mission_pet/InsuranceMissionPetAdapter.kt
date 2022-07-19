package ai.comake.petping.ui.etc.mission_pet

import ai.comake.petping.R
import ai.comake.petping.data.vo.PetInsuranceMissionPet
import ai.comake.petping.databinding.AdapterInsuranceMissionPetItemBinding
import ai.comake.petping.ui.base.BindingViewHolder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

/**
 * android-petping-2
 * Class: InsuranceMissionPetAdapter
 * Created by cliff on 2022/02/16.
 *
 * Description:
 */
class InsuranceMissionPetAdapter(
    private val viewModel: MissionPetViewModel
) : RecyclerView.Adapter<InsuranceMissionPetAdapter.ViewHolder>() {

    private var mItems:MutableList<PetInsuranceMissionPet> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.adapter_insurance_mission_pet_item,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mItems[position])
    }

    override fun getItemCount() = mItems.size

    fun setItems(items: List<PetInsuranceMissionPet>) {
        val diffCallback = DiffCallback(mItems, items.toMutableList())
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.mItems.clear()
        this.mItems.addAll(items)
        diffResult.dispatchUpdatesTo(this)
    }

    inner class ViewHolder(view: View) :
        BindingViewHolder<AdapterInsuranceMissionPetItemBinding>(view) {
        fun bind(item: PetInsuranceMissionPet) {
            binding.viewModel = viewModel
            binding.item = item
        }
    }

    inner class DiffCallback(
        private var oldList: MutableList<PetInsuranceMissionPet>,
        private var newList: MutableList<PetInsuranceMissionPet>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].petId == newList[newItemPosition].petId
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}