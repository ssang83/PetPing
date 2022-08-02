package ai.comake.petping.ui.guide

import ai.comake.petping.AppConstants
import ai.comake.petping.R
import ai.comake.petping.databinding.FragmentUserGuideBinding
import ai.comake.petping.observeEvent
import ai.comake.petping.ui.base.BaseFragment
import ai.comake.petping.util.SharedPreferencesManager
import ai.comake.petping.util.updateWhiteStatusBar
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * android-petping-2
 * Class: UserGuideFragment
 * Created by cliff on 2022/07/28.
 *
 * Description:
 */
class UserGuideFragment : BaseFragment<FragmentUserGuideBinding>(FragmentUserGuideBinding::inflate) {

    private val viewModel: UserGuideViewModel by viewModels()

    val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            finishApplication()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateWhiteStatusBar(requireActivity().window)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        requireActivity().onBackPressedDispatcher.addCallback(onBackPressedCallback)

        with(viewModel) {

            goToStart.observeEvent(viewLifecycleOwner) {
                requireActivity().findNavController(R.id.nav_main)
                    .navigate(R.id.action_userGuideFragment_to_loginGraph)

            }

            nextPage.observeEvent(viewLifecycleOwner) { position ->
                binding.userGuideViewPager.setCurrentItem(position.inc(), false)
            }
        }

        setUi()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        onBackPressedCallback.remove()
    }

    private fun setUi() {
        with(binding) {

            userGuideViewPager.apply {
                adapter = GuidePagerAdapter()
                overScrollMode = RecyclerView.OVER_SCROLL_NEVER
                orientation = ViewPager2.ORIENTATION_HORIZONTAL
            }

            TabLayoutMediator(guideIndicator, userGuideViewPager) { tab, position ->
                tab.view.isClickable = false
            }.attach()
        }
    }

    /**
     * '뒤로가기' 버튼 2회 연속 입력을 통한 종료를 사용자에게 안내하고 처리
     */
    private var backPressedTime: Long = 0
    private fun finishApplication() {
        if (System.currentTimeMillis() - backPressedTime < AppConstants.DOUBLE_BACK_PRESS_EXITING_TIME_LIMIT) {
            requireActivity().finish()
            return
        }
        Toast.makeText(requireContext(), getString(R.string.finish_app_guide), Toast.LENGTH_SHORT).show()
        backPressedTime = System.currentTimeMillis()
    }

    inner class GuidePagerAdapter : FragmentStateAdapter(this) {

        val mFragmentList:MutableList<Fragment> = mutableListOf()

        init{
            mFragmentList.apply{
                // add Fragments
                add(GuideImageFragment(R.drawable.img_guide_01))
                add(GuideImageFragment(R.drawable.img_guide_02))
                add(GuideImageFragment(R.drawable.img_guide_03))
                add(GuideImageFragment(R.drawable.img_guide_04))
                add(GuideImageFragment(R.drawable.img_guide_05))
            }
        }

        override fun getItemCount(): Int = mFragmentList.size
        override fun createFragment(position: Int): Fragment = mFragmentList[position]
    }
}