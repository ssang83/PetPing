package ai.comake.petping.ui.etc.inquiry

import ai.comake.petping.R
import ai.comake.petping.databinding.ItemInquiryImageBinding
import ai.comake.petping.ui.base.BindingViewHolder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

/**
 * android-petping-2
 * Class: InquiryImageAdapter
 * Created by cliff on 2022/06/29.
 *
 * Description:
 */
class InquiryImageAdapter(
    private val viewModel: InquiryViewModel
): RecyclerView.Adapter<InquiryImageAdapter.ViewHolder>() {

    private var mItems:MutableList<String> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_inquiry_image,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mItems[position], position)
    }

    override fun getItemCount() = mItems.size

    fun addItem(items:List<String>) {
        mItems.clear()
        mItems.addAll(items)

        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : BindingViewHolder<ItemInquiryImageBinding>(view) {
        fun bind(url: String, position: Int) {
            binding.viewModel = viewModel
            binding.url = url
            binding.position = position
        }
    }
}