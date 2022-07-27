package ai.comake.petping.ui.home.reward

import ai.comake.petping.data.vo.CompletionMission
import ai.comake.petping.data.vo.OngoingMission
import ai.comake.petping.databinding.*
import ai.comake.petping.ui.base.BaseViewHolder
import ai.comake.petping.util.*
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

/**
 * android-petping-2
 * Class: MissionAdapter
 * Created by cliff on 2022/02/11.
 *
 * Description:
 */
class MissionAdapter(private val viewModel: RewardViewModel) :
    RecyclerView.Adapter<BaseViewHolder>() {

    private val ONGOING_NORMAL_CARD_VIEW = 0
    private val ONGOING_EVENT_CARD_VIEW = 1
    private val COMPLETION_NORMAL_CARD_VIEW = 2
    private val COMPLETION_EVENT_CARD_VIEW = 3
    private val HEADER_VIEW = 4
    private val EMPTY_VIEW = 5
    private val MORE_VIEW = 6

    private val ongoingMissions: ArrayList<OngoingMission> = ArrayList()
    private val completionMissions: ArrayList<CompletionMission> = ArrayList()

    private var ongoingMissionSize = 0
    private var completionMissionSize = 0

    private val viewList = ArrayList<Int>()

    fun updateData(
        ongoingMissions: ArrayList<OngoingMission>,
        ongoingMissionSize: Int,
        completionMissions: ArrayList<CompletionMission>,
        completionMissionSize: Int
    ) {
        this.ongoingMissions.clear()
        this.ongoingMissions.addAll(ongoingMissions)
        this.ongoingMissionSize = ongoingMissionSize

        this.completionMissions.clear()
        this.completionMissions.addAll(completionMissions)
        this.completionMissionSize = completionMissionSize

        this.viewList.clear()

        when {
            ongoingMissions.size == 0 && completionMissions.size != 0 -> {
                viewList.add(EMPTY_VIEW)
            }
            ongoingMissions.size < ongoingMissionSize -> {
                viewList.add(HEADER_VIEW)
                repeat(ongoingMissions.size) {
                    if (ongoingMissions[it].type == 5) {
                        viewList.add(ONGOING_EVENT_CARD_VIEW)
                    } else {
                        viewList.add(ONGOING_NORMAL_CARD_VIEW)
                    }
                }
                viewList.add(MORE_VIEW)
            }
            else -> {
                viewList.add(HEADER_VIEW)
                repeat(ongoingMissions.size) {
                    if (ongoingMissions[it].type == 5) {
                        viewList.add(ONGOING_EVENT_CARD_VIEW)
                    } else {
                        viewList.add(ONGOING_NORMAL_CARD_VIEW)
                    }
                }
            }
        }

        when {
            completionMissions.size == 0 -> {
            }
            completionMissions.size < completionMissionSize -> {
                viewList.add(HEADER_VIEW)
                repeat(completionMissions.size) {
                    if (completionMissions[it].type == 5) {
                        viewList.add(COMPLETION_EVENT_CARD_VIEW)
                    } else {
                        viewList.add(COMPLETION_NORMAL_CARD_VIEW)
                    }
                }
                viewList.add(MORE_VIEW)
            }
            else -> {
                viewList.add(HEADER_VIEW)
                repeat(completionMissions.size) {
                    if (completionMissions[it].type == 5) {
                        viewList.add(COMPLETION_EVENT_CARD_VIEW)
                    } else {
                        viewList.add(COMPLETION_NORMAL_CARD_VIEW)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            ONGOING_EVENT_CARD_VIEW -> {
                val binding = ItemEventMissionCardBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                OngoingEventMissionViewHolder(binding)
            }
            COMPLETION_EVENT_CARD_VIEW -> {
                val binding = ItemEventMissionCardBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                CompleteEvnetMissionViewHolder(binding)
            }
            ONGOING_NORMAL_CARD_VIEW -> {
                val binding = ItemMissionCardBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                OngoingNormalMissionViewHolder(binding)
            }
            COMPLETION_NORMAL_CARD_VIEW -> {
                val binding = ItemMissionCardBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                CompletionNormalMissionViewHolder(binding)
            }
            HEADER_VIEW -> {
                val binding = ItemMissionHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                TitleViewHolder(binding)
            }
            EMPTY_VIEW -> {
                val binding =
                    ItemNoMissionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                EmptyViewHolder(binding)
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

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemViewType(position: Int): Int {
        return viewList[position]
    }

    inner class TitleViewHolder(binding: ItemMissionHeaderBinding) : BaseViewHolder(binding.root) {
        private var mBinding: ItemMissionHeaderBinding = binding

        override fun onBind(position: Int) {
            mBinding.executePendingBindings()
            if (position == 0) {
                mBinding.title.text = "진행 중인 미션"
            } else {
                mBinding.title.text = "완료 미션"
            }
        }
    }

    inner class OngoingNormalMissionViewHolder(binding: ItemMissionCardBinding) :
        BaseViewHolder(binding.root) {
        private var mCommonMissionBinding: ItemMissionCardBinding = binding

        override fun onBind(position: Int) {
            val ongoingMission: OngoingMission = ongoingMissions[position - 1]
            mCommonMissionBinding.executePendingBindings()
            var middle_text = ""
            if (ongoingMission.type == 4) middle_text = "님 "
            else middle_text = "의 "
            mCommonMissionBinding.missionType.text = ongoingMission.typeStr
            mCommonMissionBinding.description.text = ongoingMission.name
            mCommonMissionBinding.targetName.text = ongoingMission.targetName
            mCommonMissionBinding.middleText.text = middle_text
            mCommonMissionBinding.state.setOngoingState(
                ongoingMission.missionStateStr ?: "",
                mCommonMissionBinding.goBtn
            )

            when (ongoingMission.type) {
                1 -> {
                    mCommonMissionBinding.missionType.setTextColor(Color.parseColor("#48AAD9"))
                }
                2,3,4 ->{
                    mCommonMissionBinding.missionType.setTextColor(Color.parseColor("#FF4857"))
                }
            }

            mCommonMissionBinding.point.text = "${ongoingMission.reward?.toNumberFormat()}P"
            mCommonMissionBinding.period.text =
                "${ongoingMission.startDate} ~ ${ongoingMission.endDate}"
            mCommonMissionBinding.cardView.setSafeOnClickListener {
                viewModel.goToDetail(ongoingMission)
            }
        }
    }

    inner class CompletionNormalMissionViewHolder(binding: ItemMissionCardBinding) :
        BaseViewHolder(binding.root) {
        private var mBinding: ItemMissionCardBinding = binding

        override fun onBind(position: Int) {
            mBinding.executePendingBindings()
            val index = if (ongoingMissions.size == 0) {
                position - 2
            } else {
                if (ongoingMissions.size == ongoingMissionSize) {
                    position - 2 - ongoingMissions.size
                } else {
                    position - 2 - ongoingMissions.size - 1
                }
            }

            val completionMission: CompletionMission = completionMissions[index]
            var middle_text = ""
            if (completionMission.type == 4) middle_text = "님 "
            else middle_text = "의 "
            mBinding.missionType.text = completionMission.typeStr

            when (completionMission.type) {
                1 -> {
                    mBinding.missionType.setTextColor(Color.parseColor("#48AAD9"))
                }
                2,3,4 ->{
                    mBinding.missionType.setTextColor(Color.parseColor("#FF4857"))
                }
            }

            mBinding.description.text = completionMission.name
            mBinding.middleText.text = middle_text
            mBinding.targetName.text = completionMission.targetName
            mBinding.goBtn.visibility = View.GONE
            mBinding.state.setCompletionState(completionMission.missionStateStr)
            mBinding.point.text = "${completionMission.reward.toNumberFormat()}P"
            mBinding.period.text = "${completionMission.startDate} ~ ${completionMission.endDate}"

            mBinding.cardView.setSafeOnClickListener {
                viewModel.goToDetail(completionMission)
            }
        }
    }

    inner class OngoingEventMissionViewHolder(binding: ItemEventMissionCardBinding) :
        BaseViewHolder(binding.root) {
        private var itemEventMissionCardBinding: ItemEventMissionCardBinding = binding

        override fun onBind(position: Int) {
            itemEventMissionCardBinding.executePendingBindings()

            val ongoingMission: OngoingMission = ongoingMissions[position - 1]
            itemEventMissionCardBinding.rootView.setBackgroundColor(Color.parseColor(ongoingMission.missionCardDesign?.bgCode))
            itemEventMissionCardBinding.descriptionMain.text = ongoingMission.name

            if (ongoingMission.missionCardDesign?.imageURL.isNullOrEmpty()) {
                itemEventMissionCardBinding.symbolImageView.visibility = View.GONE
            } else {
                itemEventMissionCardBinding.symbolImageView.visibility = View.VISIBLE
                Glide.with(itemEventMissionCardBinding.rootView.context)
                    .load(ongoingMission.missionCardDesign?.imageURL)
                    .into(itemEventMissionCardBinding.symbolImageView)
            }

            if (ongoingMission.missionState == 1) {
                itemEventMissionCardBinding.statusImageView.visibility = View.VISIBLE
                itemEventMissionCardBinding.statusTextView.visibility = View.GONE
            } else {
                itemEventMissionCardBinding.statusImageView.visibility = View.GONE
                itemEventMissionCardBinding.statusTextView.visibility = View.VISIBLE
                itemEventMissionCardBinding.statusTextView.text = ongoingMission.missionStateStr
            }

            if (ongoingMission.missionCardDesign?.title.isNullOrEmpty()) {
                itemEventMissionCardBinding.descriptionSub1.visibility = View.GONE
            } else {
                itemEventMissionCardBinding.descriptionSub1.visibility = View.VISIBLE
                itemEventMissionCardBinding.descriptionSub1.text =
                    ongoingMission.missionCardDesign?.title
            }

            if (ongoingMission.missionCardDesign?.layoutType == 1) {
                itemEventMissionCardBinding.descriptionSub1.visibility = View.GONE
                itemEventMissionCardBinding.descriptionSub2.visibility = View.VISIBLE
                itemEventMissionCardBinding.descriptionSub2.text =
                    ongoingMission.missionCardDesign.title
            } else {
                itemEventMissionCardBinding.descriptionSub2.visibility = View.GONE
            }

            itemEventMissionCardBinding.cardView.setSafeOnClickListener {
                viewModel.goToEventDetail(ongoingMission)
            }
        }
    }

    inner class CompleteEvnetMissionViewHolder(binding: ItemEventMissionCardBinding) :
        BaseViewHolder(binding.root) {
        private var itemEventMissionCardBinding: ItemEventMissionCardBinding = binding

        override fun onBind(position: Int) {
            itemEventMissionCardBinding.executePendingBindings()
            val index = if (ongoingMissions.size == 0) {
                position - 2
            } else {
                if (ongoingMissions.size == ongoingMissionSize) {
                    position - 2 - ongoingMissions.size
                } else {
                    position - 2 - ongoingMissions.size - 1
                }
            }

            val completionMission: CompletionMission = completionMissions[index]
            itemEventMissionCardBinding.rootView.setBackgroundColor(
                Color.parseColor(
                    completionMission.missionCardDesign?.bgCode
                )
            )
            itemEventMissionCardBinding.descriptionMain.text = completionMission.name

            if (completionMission.missionCardDesign?.imageURL.isNullOrEmpty()) {
                itemEventMissionCardBinding.symbolImageView.visibility = View.GONE
            } else {
                itemEventMissionCardBinding.symbolImageView.visibility = View.VISIBLE
                Glide.with(itemEventMissionCardBinding.rootView.context)
                    .load(completionMission.missionCardDesign?.imageURL)
                    .into(itemEventMissionCardBinding.symbolImageView)
            }

            if (completionMission.missionState == 1) {
                itemEventMissionCardBinding.statusImageView.visibility = View.VISIBLE
                itemEventMissionCardBinding.statusTextView.visibility = View.GONE
            } else {
                itemEventMissionCardBinding.statusImageView.visibility = View.GONE
                itemEventMissionCardBinding.statusTextView.visibility = View.VISIBLE
                itemEventMissionCardBinding.statusTextView.text = completionMission.missionStateStr
            }

            if (completionMission.missionCardDesign?.title.isNullOrEmpty()) {
                itemEventMissionCardBinding.descriptionSub1.visibility = View.GONE
            } else {
                itemEventMissionCardBinding.descriptionSub1.visibility = View.VISIBLE
                itemEventMissionCardBinding.descriptionSub1.text =
                    completionMission.missionCardDesign?.title
            }

            if (completionMission.missionCardDesign?.layoutType == 1) {
                itemEventMissionCardBinding.descriptionSub1.visibility = View.GONE
                itemEventMissionCardBinding.descriptionSub2.visibility = View.VISIBLE
                itemEventMissionCardBinding.descriptionSub2.text =
                    completionMission.missionCardDesign?.title
            } else {
                itemEventMissionCardBinding.descriptionSub2.visibility = View.GONE
            }

            itemEventMissionCardBinding.cardView.setSafeOnClickListener {
                viewModel.goToEventDetail(completionMission)
            }
        }
    }

    inner class EmptyViewHolder(binding: ItemNoMissionBinding) : BaseViewHolder(binding.root) {
        private var mBinding: ItemNoMissionBinding = binding

        override fun onBind(position: Int) {
            mBinding.executePendingBindings()
        }
    }

    inner class MoreViewHolder(binding: ItemMoreBtnBinding) : BaseViewHolder(binding.root) {
        private var mBinding: ItemMoreBtnBinding = binding

        override fun onBind(position: Int) {
            mBinding.executePendingBindings()
            mBinding.moreBtn.setSafeOnClickListener {
                if (position + 1 == viewList.size) {
                    viewModel.getNextCompletionMission()
                } else {
                    viewModel.getNextOngoingMission()
                }
            }
        }
    }
}