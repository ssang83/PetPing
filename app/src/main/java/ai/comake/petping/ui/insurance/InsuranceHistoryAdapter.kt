package ai.comake.petping.ui.insurance

import ai.comake.petping.R
import ai.comake.petping.data.vo.PetInsurJoinsData
import ai.comake.petping.databinding.ItemPetInsuranceBinding
import ai.comake.petping.ui.base.BindingViewHolder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

/**
 * android-petping-2
 * Class: InsuranceHistoryAdapter
 * Created by cliff on 2022/03/22.
 *
 * Description:
 */
class InsuranceHistoryAdapter(
    private val viewModel: InsuranceHistoryViewModel
) : RecyclerView.Adapter<InsuranceHistoryAdapter.ViewHolder>() {

    private var mItems:MutableList<PetInsurJoinsData> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_pet_insurance,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mItems[position])
    }

    override fun getItemCount() = mItems.size

    fun setItems(items: List<PetInsurJoinsData>) {
        val diffCallback = DiffCallback(mItems, items.toMutableList())
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.mItems.clear()
        this.mItems.addAll(items)
        diffResult.dispatchUpdatesTo(this)
    }

    inner class ViewHolder(view: View) : BindingViewHolder<ItemPetInsuranceBinding>(view) {
        fun bind(item: PetInsurJoinsData) {
            binding.item = item
            binding.viewModel = viewModel
        }
    }

    inner class DiffCallback(
        private var oldList: MutableList<PetInsurJoinsData>,
        private var newList: MutableList<PetInsurJoinsData>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].url == newList[newItemPosition].url
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}