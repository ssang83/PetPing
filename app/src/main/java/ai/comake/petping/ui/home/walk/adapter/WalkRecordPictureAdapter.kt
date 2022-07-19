package ai.comake.petping.ui.home.walk.adapter

import ai.comake.petping.databinding.WalkRecordListItemAddBinding
import ai.comake.petping.databinding.WalkRecordListItemPictureBinding
import ai.comake.petping.util.setSafeOnClickListener
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class WalkRecordPictureAdapter(
    private val onPictureImageClick: (String) -> Unit,
    private val onAddImageClick: () -> Unit,
    private val onDeleteImageClick: (Int) -> Unit
) :
    ListAdapter<String, RecyclerView.ViewHolder>(TaskDiffCallback()) {
    private val ADD_VIEW = 1
    private val IMAGE_VIEW = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            IMAGE_VIEW -> {
                val binding = WalkRecordListItemPictureBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                PictureViewHolder(binding)
            }
            else -> {
                val binding = WalkRecordListItemAddBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                AddViewHolder(binding)
            }
        }
    }

    override fun getItemCount(): Int {
        return if (currentList.size == 5) {
            currentList.size
        } else {
            currentList.size + 1
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == currentList.size) {
            ADD_VIEW
        } else {
            IMAGE_VIEW
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            ADD_VIEW -> (holder as AddViewHolder).bind()
            IMAGE_VIEW -> (holder as PictureViewHolder).bind(getItem(position))
        }
    }

    inner class PictureViewHolder(binding: WalkRecordListItemPictureBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private var mBinding: WalkRecordListItemPictureBinding = binding

        fun bind(item: String) {
            mBinding.item = item
            mBinding.pictureImageView.setSafeOnClickListener {
                onPictureImageClick(item)
            }
            mBinding.deletePictureButton.setSafeOnClickListener {
                onDeleteImageClick(layoutPosition)
            }
        }
    }

    inner class AddViewHolder(private val binding: WalkRecordListItemAddBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.addButton.setSafeOnClickListener {
                onAddImageClick()
            }
        }
    }

    class TaskDiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean {
            return oldItem == newItem
        }
    }

}