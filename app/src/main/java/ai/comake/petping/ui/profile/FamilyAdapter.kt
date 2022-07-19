package ai.comake.petping.ui.profile

import ai.comake.petping.R
import ai.comake.petping.data.vo.MemberInfoFamilyReg
import ai.comake.petping.databinding.ItemFamilyViewBinding
import ai.comake.petping.ui.base.BindingViewHolder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

/**
 * android-petping-2
 * Class: FamilyAdapter
 * Created by cliff on 2022/03/24.
 *
 * Description:
 */
class FamilyAdapter(
    private val viewModel: SettingFamilyViewModel
): RecyclerView.Adapter<FamilyAdapter.ViewHolder>() {

    private var mItems: MutableList<MemberInfoFamilyReg> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_family_view,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mItems[position])
    }

    override fun getItemCount() = mItems.size

    fun setItems(items: List<MemberInfoFamilyReg>) {
        val diffCallback = DiffCallback(mItems, items.toMutableList())
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.mItems.clear()
        this.mItems.addAll(items)
        diffResult.dispatchUpdatesTo(this)
    }

    inner class ViewHolder(view: View) : BindingViewHolder<ItemFamilyViewBinding>(view) {
        fun bind(item: MemberInfoFamilyReg) {
            binding.item = item
            binding.viewModel = viewModel
        }
    }

    inner class DiffCallback(
        private var oldList: MutableList<MemberInfoFamilyReg>,
        private var newList: MutableList<MemberInfoFamilyReg>
    ): DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].profileId == newList[newItemPosition].profileId
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}