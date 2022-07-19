package ai.comake.petping.ui.home.walk.adapter

import ai.comake.petping.Event
import ai.comake.petping.data.vo.WalkablePet
import ai.comake.petping.databinding.BottomSheetDialogWalkPetListItemBinding
import ai.comake.petping.ui.home.walk.dialog.WalkablePetDialogViewModel
import ai.comake.petping.util.LogUtil
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class WalkablePetRecyclerViewAdapter(
    val walkablePetDialogViewModel: WalkablePetDialogViewModel
) :
    ListAdapter<WalkablePet.Pets, WalkablePetRecyclerViewAdapter.ViewHolder>(TaskDiffCallback()) {
    private var selectedPetIds = ArrayList<WalkablePet.Pets>()

    init {
        LogUtil.log("TAG", "")
        selectedPetIds = arrayListOf()
    }

    var isCheckedAllTogether: Boolean = false

    inner class ViewHolder(
        private val binding: BottomSheetDialogWalkPetListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(viewModel: WalkablePetDialogViewModel, item: WalkablePet.Pets) {
            binding.viewModel = viewModel
            binding.data = item

            setUpView(item)
            setOnClickListener(item)
        }

        private fun setOnClickListener(item: WalkablePet.Pets) {
            binding.rootView.setOnClickListener {
                if (item.id == -1) {
                    isCheckedAllTogether = !isCheckedAllTogether
                    if (isCheckedAllTogether) {
                        addAllSelect()
                    } else {
                        clearAllSelect()
                    }
                    notifyDataSetChanged()
                } else {
                    if (!binding.selCheckbox.isChecked) {
                        selectedPetIds.add(item)
                    } else {
                        selectedPetIds.remove(item)
                    }

                    if (selectedPetIds.size > 5) {
                        selectedPetIds.remove(item)
                        walkablePetDialogViewModel._isOverPetMaxSize.postValue(Event(true))
                    } else {
                        binding.selCheckbox.isChecked = !binding.selCheckbox.isChecked
                        walkablePetDialogViewModel._selectedPetIds.postValue(Event(selectedPetIds))
                    }
                }
            }
        }

        private fun setUpView(item: WalkablePet.Pets) {
            if (item.id == -1) {
                Glide.with(binding.profileImage.context).load(item.allTogetherImageURL)
                    .into(binding.profileImage)
            }
            binding.selCheckbox.isChecked = isCheckedAllTogether
        }
    }

    private fun addAllSelect() {
        currentList.forEach { item ->
            selectedPetIds.add(item)
        }
        walkablePetDialogViewModel._selectedPetIds.postValue(Event(selectedPetIds))
    }

    private fun clearAllSelect() {
        selectedPetIds.clear()
        walkablePetDialogViewModel._selectedPetIds.postValue(Event(selectedPetIds))
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        LogUtil.log("TAG", "")
        return ViewHolder(
            BottomSheetDialogWalkPetListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(walkablePetDialogViewModel, getItem(position))
    }

    class TaskDiffCallback : DiffUtil.ItemCallback<WalkablePet.Pets>() {
        override fun areItemsTheSame(
            oldItem: WalkablePet.Pets,
            newItem: WalkablePet.Pets
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: WalkablePet.Pets,
            newItem: WalkablePet.Pets
        ): Boolean {
            return oldItem == newItem
        }
    }
}