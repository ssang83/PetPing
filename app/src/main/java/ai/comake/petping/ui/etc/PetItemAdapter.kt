package ai.comake.petping.ui.etc

import ai.comake.petping.R
import ai.comake.petping.data.vo.MyPet
import ai.comake.petping.databinding.ItemMyPetAddBinding
import ai.comake.petping.databinding.ItemMyPetBinding
import ai.comake.petping.ui.base.BindingViewHolder
import ai.comake.petping.util.LogUtil
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.log

/**
 * android-petping-2
 * Class: PetItemAdapter
 * Created by cliff on 2022/02/22.
 *
 * Description:
 */
class PetItemAdapter(
    private val viewModel: EtcViewModel
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val ITEM_VIEW = 0
    private val FOOTER_VIEW = 1

    private var mItems: MutableList<MyPet> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ITEM_VIEW) {
            ItemViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_my_pet,
                    parent,
                    false
                )
            )
        } else {
            FooterViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_my_pet_add,
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            ITEM_VIEW -> (holder as ItemViewHolder).bind(mItems[position])
            FOOTER_VIEW -> (holder as FooterViewHolder).bind()
        }
    }

    override fun getItemCount(): Int {
        return if (mItems.size < 10) mItems.size + 1
        else mItems.size
    }

    override fun getItemViewType(position: Int): Int {
        LogUtil.log("TAG", "position $position")
        return if (mItems.size < 10) {
            if (position == mItems.size) {
                FOOTER_VIEW
            } else {
                ITEM_VIEW
            }
        } else {
            ITEM_VIEW
        }

//        when (position == mItems.size) {
//            true -> {
//                return if (mItems.size >= 10) ITEM_VIEW
//                else FOOTER_VIEW
//            }
//            else -> ITEM_VIEW
//        }
    }

    fun setItems(items: List<MyPet>) {
        val diffCallback = DiffCallback(mItems, items.toMutableList())
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.mItems.clear()
        this.mItems.addAll(items)
        diffResult.dispatchUpdatesTo(this)
    }

    inner class ItemViewHolder(view: View) : BindingViewHolder<ItemMyPetBinding>(view) {
        fun bind(item: MyPet) {
            binding.viewModel = viewModel
            binding.item = item
        }
    }

    inner class FooterViewHolder(view: View) : BindingViewHolder<ItemMyPetAddBinding>(view) {
        fun bind() {
            binding.viewModel = viewModel
        }
    }

    inner class DiffCallback(
        private var oldList: MutableList<MyPet>,
        private var newList: MutableList<MyPet>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}