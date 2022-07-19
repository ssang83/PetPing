package ai.comake.petping.ui.history.reward

import ai.comake.petping.*
import ai.comake.petping.databinding.FragmentRewardHistoryBinding
import ai.comake.petping.ui.base.BaseFragment
import ai.comake.petping.ui.history.reward.saving.SavingFragment
import ai.comake.petping.ui.history.reward.using.UsingFragment
import ai.comake.petping.util.backStack
import ai.comake.petping.util.setSafeOnClickListener
import ai.comake.petping.util.updateWhiteStatusBar
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

/**
 * android-petping-2
 * Class: RewardHistoryFragment
 * Created by cliff on 2022/02/10.
 *
 * Description:
 */
const val FROM_SHOP = "from_shop"

@AndroidEntryPoint
class RewardHistoryFragment :
    BaseFragment<FragmentRewardHistoryBinding>(FragmentRewardHistoryBinding::inflate) {

    private val viewModel by viewModels<RewardHistoryViewModel>()
    private val mainShareViewModel: MainShareViewModel by activityViewModels()

    private lateinit var pagerAdapter: HistoryPagerAdapter

    private val fromShop by lazy {
        arguments?.getBoolean(FROM_SHOP, false) ?: false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateWhiteStatusBar(requireActivity().window)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        viewModel.loadData()
        viewModel.uiState.observeEvent(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> mainShareViewModel.showPopUp.emit(true)
                else -> mainShareViewModel.showPopUp.emit(false)
            }
        }

        with(binding) {
            viewPagerContainer.apply {
                pagerAdapter = HistoryPagerAdapter()
                adapter = pagerAdapter
                isUserInputEnabled = false
            }

            TabLayoutMediator(tabLayout, viewPagerContainer) { tab, position ->
                when (position) {
                    0 -> tab.text = getString(R.string.saving_ping)
                    else -> tab.text = getString(R.string.using_ping)
                }
            }.attach()

            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    viewPagerContainer.setCurrentItem(tab!!.position, false)
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {}
                override fun onTabUnselected(tab: TabLayout.Tab?) {}
            })

            header.btnBack.setSafeOnClickListener {
                requireActivity().backStack(R.id.nav_main)
            }
        }

        setTabLayoutFont()

        if (fromShop) {
            binding.viewPagerContainer.setCurrentItem(1, false)
        }
    }

    private fun setTabLayoutFont() {
        val vg = binding.tabLayout.getChildAt(0) as ViewGroup
        val tabCnt = vg.childCount

        for (i in 0 until tabCnt) {
            val vgTab = vg.getChildAt(i) as ViewGroup
            val tabChildCnt = vgTab.childCount
            for (j in 0 until tabChildCnt) {
                val tabViewChild = vgTab.getChildAt(j)
                if (tabViewChild is AppCompatTextView) {
                    tabViewChild.typeface = Typeface.createFromAsset(requireContext().assets, "nanum_square_round_b.ttf")
                }
            }
        }
    }

    inner class HistoryPagerAdapter : FragmentStateAdapter(this) {

        private val mFragmentList:MutableList<Fragment> = mutableListOf()

        init {
            mFragmentList.apply {
                add(SavingFragment())
                add(UsingFragment())
            }
        }

        override fun getItemCount() = mFragmentList.size
        override fun createFragment(position: Int) = mFragmentList[position]
    }
}