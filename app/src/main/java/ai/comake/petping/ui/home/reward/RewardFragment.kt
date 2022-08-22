package ai.comake.petping.ui.home.reward

import ai.comake.petping.AirbridgeManager
import ai.comake.petping.R
import ai.comake.petping.data.vo.WebConfig
import ai.comake.petping.databinding.FragmentRewardBinding
import ai.comake.petping.observeEvent
import ai.comake.petping.ui.home.HomeFragmentDirections
import ai.comake.petping.util.LogUtil
import ai.comake.petping.util.updateWhiteStatusBar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import co.ab180.airbridge.Airbridge
import co.ab180.airbridge.event.model.SemanticAttributes
import dagger.hilt.android.AndroidEntryPoint

/**
 * android-petping-2
 * Class: RewardFragment
 * Created by cliff on 2022/02/09.
 *
 * Description:
 */
@AndroidEntryPoint
class RewardFragment : Fragment() {
    private lateinit var binding: FragmentRewardBinding
    private val viewModel by viewModels<RewardViewModel>()

    private lateinit var mAdapter:MissionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AirbridgeManager.trackEvent(
            "point_category",
            "point_action",
            "point_label"
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRewardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        LogUtil.log("TAG","onViewCreated")
//        updateWhiteStatusBar(requireActivity().window)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        with(viewModel) {

            loadData()

            updateMission.observeEvent(viewLifecycleOwner) {
                if (updateCompletion && updateOngoing) {
                    updateMissions()
                }
            }

            moveToHistory.observeEvent(viewLifecycleOwner) {
                val eventValue = 10f
                val eventAttributes = mutableMapOf<String, String>()
                val semanticAttributes = SemanticAttributes()
                Airbridge.trackEvent(
                    "rewardviewed_upper_detailed_click",
                    "rewardviewed_upper_detailed_click_action",
                    "rewardviewed_upper_detailed_click_label",
                    eventValue,
                    eventAttributes,
                    semanticAttributes.toMap()
                )

                requireActivity().findNavController(R.id.nav_main)
                    .navigate(R.id.action_homeScreen_to_rewardHistoryFragment)
            }

            moveToOngoingDetail.observeEvent(viewLifecycleOwner) { ongoing ->
                val eventValue = 10f
                val eventAttributes = mutableMapOf<String, String>()
                val semanticAttributes = SemanticAttributes()
                Airbridge.trackEvent(
                    "rewardviewed_missionlist_click",
                    "rewardviewed_missionlist_click_action",
                    "rewardviewed_missionlist_click_label",
                    eventValue,
                    eventAttributes,
                    semanticAttributes.toMap()
                )

                requireActivity().findNavController(R.id.nav_main)
                    .navigate(HomeFragmentDirections.actionHomeScreenToRewardWebFragment(ongoing))
            }

            moveToCompletionDetail.observeEvent(viewLifecycleOwner) { completion ->
                val eventValue = 10f
                val eventAttributes = mutableMapOf<String, String>()
                val semanticAttributes = SemanticAttributes()
                Airbridge.trackEvent(
                    "rewardviewed_missionlist_click",
                    "rewardviewed_missionlist_click_action",
                    "rewardviewed_missionlist_click_label",
                    eventValue,
                    eventAttributes,
                    semanticAttributes.toMap()
                )

                val config = WebConfig(
                    url = completion.missionDetailURL
                )
                requireActivity().findNavController(R.id.nav_main)
                    .navigate(HomeFragmentDirections.actionHomeScreenToContentsWebFragment(config))
            }

            moveToOngoingEventDetail.observeEvent(viewLifecycleOwner) { ongoing ->
                val config = WebConfig(
                    url = ongoing.missionDetailURL!!
                )
                requireActivity().findNavController(R.id.nav_main)
                    .navigate(HomeFragmentDirections.actionHomeScreenToEventWebFragment(config))
            }

            moveToCompletionEventDetail.observeEvent(viewLifecycleOwner) { completion ->
                val config = WebConfig(
                    url = completion.missionDetailURL
                )
                requireActivity().findNavController(R.id.nav_main)
                    .navigate(HomeFragmentDirections.actionHomeScreenToEventWebFragment(config))
            }

            moveToMissionPet.observeEvent(viewLifecycleOwner) {
                requireActivity().findNavController(R.id.nav_main)
                    .navigate(R.id.action_homeScreen_to_missionPetFragment)
            }
        }

        setUp()
    }

    private fun setUp() {
        mAdapter = MissionAdapter(viewModel)
        binding.recyclerMissions.apply {
            layoutManager = LinearLayoutManager(requireContext()).apply { orientation = LinearLayoutManager.VERTICAL }
            itemAnimator = DefaultItemAnimator()
            adapter = mAdapter
        }
    }

    private fun updateMissions() {
        LogUtil.log("TAG", "updateMissions")
        binding.recyclerMissions.apply {
            viewModel.ongoingMissionData?.let { ongoing ->
                viewModel.completionMissionData?.let { completion ->
                    mAdapter.updateData(
                        viewModel.ongoingMissions,
                        ongoing.listSize.toInt(),
                        viewModel.completionMissions,
                        completion.listSize.toInt()
                    )
                }
            }

            mAdapter.notifyDataSetChanged()
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        LogUtil.log("TAG","show ${!hidden}")
    }

    companion object {
        const val TAG = "RewardFragment"
    }
}