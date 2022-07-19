package ai.comake.petping.ui.common.dialog

import ai.comake.petping.databinding.ItemSelectBinding
import ai.comake.petping.ui.base.BaseViewHolder
import ai.comake.petping.util.setSafeOnClickListener
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * android-petping-2
 * Class: SelectorAdapter
 * Created by cliff on 2022/03/21.
 *
 * Description:
 */
class SelectorAdapter(
    private val data: List<String>,
    private val initial: String,
    private val listener: (title: String) -> Unit
) : RecyclerView.Adapter<BaseViewHolder>() {

    private var selectedPosition = data.indexOf(initial)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding =
            ItemSelectBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SelectItemViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    inner class SelectItemViewHolder(binding: ItemSelectBinding) : BaseViewHolder(binding.root) {
        private var mBinding: ItemSelectBinding = binding

        override fun onBind(position: Int) {
            mBinding.executePendingBindings()
            mBinding.title.text = data[position]
            mBinding.itemRoot.setSafeOnClickListener {
                if (!mBinding.selRadio.isSelected) {
                    val prevPosition = selectedPosition
                    selectedPosition = position
                    mBinding.selRadio.isSelected = true
                    notifyItemChanged(prevPosition)
                    listener.invoke(data[position])
                }
            }
            mBinding.selRadio.isSelected = selectedPosition == position
        }
    }
}