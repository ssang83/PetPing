package ai.comake.petping.ui.history.reward.using

import ai.comake.petping.data.vo.UsingHistory
import ai.comake.petping.databinding.ItemMoreBtnBinding
import ai.comake.petping.databinding.ItemUsingCardBinding
import ai.comake.petping.ui.base.BaseViewHolder
import ai.comake.petping.util.setSafeOnClickListener
import ai.comake.petping.util.toNumberFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * android-petping-2
 * Class: UsingItemAdapter
 * Created by cliff on 2022/02/10.
 *
 * Description:
 */
class UsingItemAdapter(val viewModel: UsingViewModel) :
    RecyclerView.Adapter<BaseViewHolder>() {

    private val USING_CARD = 0
    private val MORE_VIEW = 2

    private val usingHistories = ArrayList<UsingHistory>()
    private var usingHistorySize: Int = 0

    private val viewList = ArrayList<Int>()

    fun updateData(
        usingHistories: ArrayList<UsingHistory>,
        usingHistorySize: Int
    ) {
        this.usingHistories.clear()
        this.usingHistories.addAll(usingHistories)
        this.usingHistorySize = usingHistorySize

        this.viewList.clear()

        when {
            usingHistories.size == 0 -> {
            }
            usingHistories.size < usingHistorySize -> {
                repeat(usingHistories.size) { viewList.add(USING_CARD) }
                viewList.add(MORE_VIEW)
            }
            else -> {
                repeat(usingHistories.size) { viewList.add(USING_CARD) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            USING_CARD -> {
                val binding = ItemUsingCardBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                UsingItemHolder(binding)
            }
            else -> {
                val binding =
                    ItemMoreBtnBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                MoreViewHolder(binding)
            }
        }
    }

    override fun getItemCount(): Int {
        return viewList.size
    }

    override fun getItemViewType(position: Int): Int {
        return viewList[position]
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    inner class UsingItemHolder(binding: ItemUsingCardBinding) :
        BaseViewHolder(binding.root) {
        private var mBinding: ItemUsingCardBinding = binding

        override fun onBind(position: Int) {
            mBinding.executePendingBindings()
            val usingHistory = usingHistories[position]
            mBinding.date.text = usingHistory.useDate
            mBinding.title.text = usingHistory.description
            mBinding.point.text = "-" + usingHistory.reward.toNumberFormat() + "P"


        }
    }

    inner class MoreViewHolder(binding: ItemMoreBtnBinding) : BaseViewHolder(binding.root) {
        private var mBinding: ItemMoreBtnBinding = binding

        override fun onBind(position: Int) {
            mBinding.executePendingBindings()
            mBinding.moreBtn.setSafeOnClickListener {
                viewModel.getNextUsingPoints()
            }
        }
    }
}