package ai.comake.petping.ui.home.dashboard

import ai.comake.petping.R
import ai.comake.petping.data.vo.Pet
import ai.comake.petping.databinding.ItemPetAddBinding
import ai.comake.petping.databinding.ItemPetProfileBinding
import ai.comake.petping.ui.base.BindingViewHolder
import ai.comake.petping.util.setSafeOnClickListener
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.preferences.core.preferencesOf
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

/**
 * android-petping-2
 * Class: PetAdapter
 * Created by cliff on 2022/05/20.
 *
 * Description:
 */
class PetAdapter(
    val context: Context,
    val viewModel: DashboardViewModel,
    var data: List<Pet>,
    val listener: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val ITEM_VIEW = 0
    private val FOOTER_VIEW = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ITEM_VIEW) {
            ItemViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_pet_profile,
                    parent,
                    false
                )
            )
        } else {
            FooterViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_pet_add,
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            ITEM_VIEW -> (holder as ItemViewHolder).bind(data[position])
            else -> (holder as FooterViewHolder).bind()
        }
    }

    override fun getItemCount(): Int {
        return if (data.size < 9) data.size + 1
        else data.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (position == data.size) {
            true -> FOOTER_VIEW
            else -> ITEM_VIEW
        }
    }

    inner class ItemViewHolder(view: View) : BindingViewHolder<ItemPetProfileBinding>(view) {
        fun bind(item: Pet) {
            binding.viewModel = viewModel
            binding.item = item

            binding.root.setSafeOnClickListener {
                viewModel.getDashboard(context, item.profileId.toInt())
                listener.invoke()
            }
        }
    }

    inner class FooterViewHolder(view: View) : BindingViewHolder<ItemPetAddBinding>(view) {
        fun bind() {
            binding.root.setSafeOnClickListener {
                viewModel.goToMakeProfile()
                listener.invoke()
            }
        }
    }
}