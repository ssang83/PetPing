package ai.comake.petping.ui.history.reward.saving

import ai.comake.petping.R
import ai.comake.petping.data.vo.ExpiredHistory
import ai.comake.petping.data.vo.SavingHistory
import ai.comake.petping.databinding.ItemMissionHeaderBinding
import ai.comake.petping.databinding.ItemMoreBtnBinding
import ai.comake.petping.databinding.ItemSavingCardBinding
import ai.comake.petping.ui.base.BaseViewHolder
import ai.comake.petping.util.LogUtil
import ai.comake.petping.util.setSafeOnClickListener
import ai.comake.petping.util.toNumberFormat
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

/**
 * android-petping-2
 * Class: SavingItemAdapter
 * Created by cliff on 2022/02/10.
 *
 * Description:
 */
class SavingItemAdapter(val viewModel: SavingViewModel) :
    RecyclerView.Adapter<BaseViewHolder>() {

    private val SAVING_CARD_VIEW = 0
    private val EXPIRED_CARD_VIEW = 1
    private val HEADER_VIEW = 2
    private val MORE_VIEW = 3

    private val savingHistories = ArrayList<SavingHistory>()
    private var savingHistorySize: Int = 0
    private val expiredHistories = ArrayList<ExpiredHistory>()
    private var expiredHistorySize: Int = 0

    private val viewList = ArrayList<Int>()

    fun updateData(
        savingHistories: ArrayList<SavingHistory>,
        savingHistorySize: Int,
        expiredHistories: ArrayList<ExpiredHistory>,
        expiredHistorySize: Int
    ) {
        this.savingHistories.clear()
        this.savingHistories.addAll(savingHistories)
        this.savingHistorySize = savingHistorySize

        this.expiredHistories.clear()
        this.expiredHistories.addAll(expiredHistories)
        this.expiredHistorySize = expiredHistorySize

        this.viewList.clear()

        when {
            savingHistories.size == 0 -> {
            }
            savingHistories.size < savingHistorySize -> {
                repeat(savingHistories.size) { viewList.add(SAVING_CARD_VIEW) }
                viewList.add(MORE_VIEW)
            }
            else -> {
                repeat(savingHistories.size) { viewList.add(SAVING_CARD_VIEW) }
            }
        }

        when {
            expiredHistories.size == 0 -> {
            }
            expiredHistories.size < expiredHistorySize -> {
                viewList.add(HEADER_VIEW)
                repeat(expiredHistories.size) { viewList.add(EXPIRED_CARD_VIEW) }
                viewList.add(MORE_VIEW)
            }
            else -> {
                viewList.add(HEADER_VIEW)
                repeat(expiredHistories.size) { viewList.add(EXPIRED_CARD_VIEW) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            SAVING_CARD_VIEW -> {
                val binding = ItemSavingCardBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                SavingItemViewHolder(binding)
            }
            EXPIRED_CARD_VIEW -> {
                val binding = ItemSavingCardBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                ExpiredItmViewHolder(binding)
            }
            HEADER_VIEW -> {
                val binding = ItemMissionHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                TitleViewHolder(binding)
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

    inner class SavingItemViewHolder(binding: ItemSavingCardBinding) :
        BaseViewHolder(binding.root) {
        private var mBinding: ItemSavingCardBinding = binding

        override fun onBind(position: Int) {
            mBinding.executePendingBindings()
            val savingHistory = savingHistories[position]

            //refund ping
            var prefixDescription = ""
            if(savingHistory.type != 99) {
                prefixDescription = "의 "
            }
            mBinding.description.text=prefixDescription + savingHistory.description
            //refund ping

            mBinding.date.text = savingHistory.saveDate
            mBinding.targetName.text = savingHistory.targetName
            //mBinding.description.text = "의 " + savingHistory.description
            mBinding.period.text = savingHistory.useStartDate + " ~ " + savingHistory.useEndDate
            if (savingHistory.isExpireSoon) {
                mBinding.period.setTextColor(
                    ContextCompat.getColor(
                        mBinding.period.context,
                        R.color.primary_pink
                    )
                )
            }
            mBinding.type.text = savingHistory.typeStr

            when (savingHistory.type) {
                1 -> {
                    mBinding.type.visibility = View.VISIBLE
                    mBinding.type.setTextColor(Color.parseColor("#48AAD9"))
                }
                2, 3, 4 -> {
                    mBinding.type.visibility = View.VISIBLE
                    mBinding.type.setTextColor(Color.parseColor("#FF4857"))
                }
                else -> {
                    mBinding.type.visibility = View.GONE
                }
            }

            mBinding.point.text = "+ " + savingHistory.reward.toNumberFormat() + "P"
        }
    }

    inner class ExpiredItmViewHolder(binding: ItemSavingCardBinding) :
        BaseViewHolder(binding.root) {
        private var mBinding: ItemSavingCardBinding = binding

        override fun onBind(position: Int) {
            mBinding.executePendingBindings()
            val index = when {
                savingHistories.size < savingHistorySize -> {
                    position - savingHistories.size - 2
                }
                else -> {
                    position - savingHistories.size - 1
                }
            }

            val expiredHistory = expiredHistories[index]

            //refund ping
            var prefixDescription = ""
            if(expiredHistory.type.toInt() != 99) {
                prefixDescription = "의 "
            }
            mBinding.description.text = prefixDescription + expiredHistory.description
            //refund ping

            mBinding.date.text = expiredHistory.saveDate
            mBinding.targetName.text = expiredHistory.targetName
            //mBinding.description.text = "의 " + expiredHistory.description
            mBinding.period.text = expiredHistory.useStartDate + " ~ " + expiredHistory.useEndDate
            mBinding.period.setTextColor(
                ContextCompat.getColor(
                    mBinding.period.context,
                    R.color.primary_pink
                )
            )
            mBinding.type.text = expiredHistory.typeStr

            LogUtil.log("TAG","expiredHistory.type ${expiredHistory.type}")

            when (expiredHistory.type) {
                1 -> {
                    LogUtil.log("TAG","expiredHistory.type 1 ->")
                    mBinding.type.setTextColor(Color.parseColor("#48AAD9"))
                }
                2,3,4 ->{
                    mBinding.type.setTextColor(Color.parseColor("#FF4857"))
                }
                else -> {
                    mBinding.type.setTextColor(Color.parseColor("#6168D6"))
                }
            }

            mBinding.point.text = "+ " + expiredHistory.reward.toNumberFormat() + "P"
        }
    }

    inner class MoreViewHolder(binding: ItemMoreBtnBinding) : BaseViewHolder(binding.root) {
        private var mBinding: ItemMoreBtnBinding = binding

        override fun onBind(position: Int) {
            mBinding.executePendingBindings()
            mBinding.moreBtn.setSafeOnClickListener {
                if ((position + 1 == viewList.size) && (expiredHistories.size > 0)) {
                    viewModel.getNextExpirationPoints()
                } else {
                    viewModel.getNextSavingPoints()
                }
            }
        }
    }

    inner class TitleViewHolder(binding: ItemMissionHeaderBinding) : BaseViewHolder(binding.root) {
        private var mBinding: ItemMissionHeaderBinding = binding

        override fun onBind(position: Int) {
            mBinding.executePendingBindings()
            mBinding.title.text = "사용 기간 만료"
        }
    }
}