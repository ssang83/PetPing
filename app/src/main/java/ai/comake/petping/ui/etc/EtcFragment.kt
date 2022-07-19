package ai.comake.petping.ui.etc

import ai.comake.petping.AirbridgeManager
import ai.comake.petping.R
import ai.comake.petping.data.vo.MyPageData
import ai.comake.petping.databinding.FragmentEtcBinding
import ai.comake.petping.observeEvent
import ai.comake.petping.ui.base.BaseFragment
import ai.comake.petping.util.updateWhiteStatusBar
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import co.ab180.airbridge.Airbridge
import co.ab180.airbridge.event.model.SemanticAttributes
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlin.random.Random

/**
 * android-petping-2
 * Class: EtcFragment
 * Created by cliff on 2022/02/18.
 *
 * Description:
 */
@AndroidEntryPoint
class EtcFragment : BaseFragment<FragmentEtcBinding>(FragmentEtcBinding::inflate) {

    private val viewModel: EtcViewModel by viewModels()

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putIntArray(
            "SCROLL_POSITION",
            intArrayOf(binding.scrollView.scrollX, binding.scrollView.scrollY)
        )
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.let { state ->
            val position = state.getIntArray("SCROLL_POSITION")
            if (position != null) binding.scrollView.post( {
                binding.scrollView.scrollTo(
                    position[0],
                    position[1]
                )
            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AirbridgeManager.trackEvent(
            "homeviewd_gnb_more_click",
            "homeviewd_gnb_more_click_action",
            "homeviewd_gnb_more_click_label"
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateWhiteStatusBar(requireActivity().window)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        with(viewModel) {

            loadData()

            uiSetUp.observeEvent(viewLifecycleOwner) {
                setUpBanner()
            }

            moveToMemberInfo.observeEvent(viewLifecycleOwner) {
                AirbridgeManager.trackEvent(
                    "userinfoviewed_gnbmore_click",
                    "userinfoviewed_gnbmore_click_action",
                    "userinfoviewed_gnbmore_click_label"
                )

                requireActivity().findNavController(R.id.nav_main)
                    .navigate(R.id.action_etcFragment_to_memberInfoFragment)
            }

            moveToNotice.observeEvent(viewLifecycleOwner) {
                AirbridgeManager.trackEvent(
                    "notice_gnbmore_click",
                    "notice_gnbmore_click_action",
                    "notice_gnbmore_click_label"
                )

                requireActivity().findNavController(R.id.nav_main)
                    .navigate(R.id.action_etcFragment_to_noticeFragment)
            }

            moveToMissionPet.observeEvent(viewLifecycleOwner) {
                AirbridgeManager.trackEvent(
                    "titlemissionmonkey_gnbmore_click",
                    "titlemissionmonkey_gnbmore_click_action",
                    "titlemissionmonkey_gnbmore_click_label"
                )

                requireActivity().findNavController(R.id.nav_main)
                    .navigate(R.id.action_etcFragment_to_missionPetFragment)
            }

            moveToAppInfo.observeEvent(viewLifecycleOwner) {
                AirbridgeManager.trackEvent(
                    "appinfo_gnbmore_notice",
                    "appinfo_gnbmore_notice_action",
                    "appinfo_gnbmore_notice_label"
                )

                requireActivity().findNavController(R.id.nav_main)
                    .navigate(R.id.action_etcFragment_to_appSettingFragment)
            }

            moveToMakeProfile.observeEvent(viewLifecycleOwner) {
                AirbridgeManager.trackEvent(
                    "monkeyadd_gnbmore_click",
                    "monkeyadd_gnbmore_click_action",
                    "monkeyadd_gnbmore_click_label"
                )

                requireActivity().findNavController(R.id.nav_main)
                    .navigate(EtcFragmentDirections.actionEtcFragmentToProfileGraph(true))
            }

            moveToPetProfile.observeEvent(viewLifecycleOwner) { config ->
                AirbridgeManager.trackEvent(
                    "monkeyprofile_gnbmore_click",
                    "monkeyprofile_gnbmore_click_action",
                    "monkeyprofile_gnbmore_click_label"
                )

                requireActivity().findNavController(R.id.nav_main)
                    .navigate(EtcFragmentDirections.actionEtcFragmentToDogProfileFragment(config))
            }

            moveToInquiry.observeEvent(viewLifecycleOwner) {
                AirbridgeManager.trackEvent(
                    "contact_gnbmore_notice",
                    "contact_gnbmore_notice_action",
                    "contact_gnbmore_notice_label",
                )

                requireActivity().findNavController(R.id.nav_main)
                    .navigate(R.id.action_etcFragment_to_questionFragment)
            }

            moveToWelcomeKit.observeEvent(viewLifecycleOwner) {
                requireActivity().findNavController(R.id.nav_main)
                    .navigate(R.id.action_etcFragment_to_welcomKitGuideFragment)
            }
        }
    }

    private fun setUpBanner() {
        binding.apply {
            viewPager.apply {
                adapter = ScreenSlidePagerAdapter(requireActivity(), viewModel?.etcFragmentInfo!!)
            }

            if (viewModel?.etcFragmentInfo?.popupInfos?.size ?: 0 > 0) {
                viewPager.setCurrentItem(
                    Random.nextInt(0, viewModel?.etcFragmentInfo?.popupInfos?.size ?: 0),
                    false
                )
            } else {
                viewPager.visibility = View.GONE
            }

            TabLayoutMediator(indicator, viewPager) { _tab, _position -> }.attach()

            if (viewModel?.etcFragmentInfo?.popupInfos?.size ?: 0 < 2) {
                indicator.visibility = View.GONE
            }
        }
    }

    /**
     * 하단 배너
     */
    private inner class ScreenSlidePagerAdapter(fa: FragmentActivity, val data: MyPageData?) :
        FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = data?.popupInfos?.size ?: 0

        override fun createFragment(position: Int): Fragment {
            val data = data?.popupInfos?.get(position)
            return BannerFragment(
                data?.imageURL ?: "", data?.contentURL ?: "", data?.bgCode
                    ?: "#fff"
            )
        }
    }
}