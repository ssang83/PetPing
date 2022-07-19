package ai.comake.petping.ui.profile

import ai.comake.petping.R
import ai.comake.petping.databinding.FragmentSettingBinding
import ai.comake.petping.observeEvent
import ai.comake.petping.ui.base.BaseFragment
import ai.comake.petping.util.backStack
import ai.comake.petping.util.setSafeOnClickListener
import ai.comake.petping.util.updateWhiteStatusBar
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.children
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

/**
 * android-petping-2
 * Class: SettingFragment
 * Created by cliff on 2022/03/23.
 *
 * Description:
 */
@AndroidEntryPoint
class SettingFragment : BaseFragment<FragmentSettingBinding>(FragmentSettingBinding::inflate) {

    private val viewModel: SettingViewModel by viewModels()
    private val arg: SettingFragmentArgs by navArgs()

    private var pagerAdapter: SettingTabPagerAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateWhiteStatusBar(requireActivity().window)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        with(viewModel) {

            tabPosition.observeEvent(viewLifecycleOwner) { position ->
                binding.viewPagerContainer.setCurrentItem(position, false)
            }
        }

        setUpUi()
    }

    private fun setUpUi() {

        with(binding) {
            viewPagerContainer.apply {
                pagerAdapter = SettingTabPagerAdapter()
                adapter = pagerAdapter
                isUserInputEnabled = false
                offscreenPageLimit = 1
            }

            TabLayoutMediator(tabLayout, viewPagerContainer) { tab, position ->
                when (position) {
                    0 -> tab.text = getString(R.string.setting_profile)
                    1 -> tab.text = getString(R.string.setting_character)
                    else -> tab.text = getString(R.string.setting_family)
                }
            }.attach()

            btnBack.setSafeOnClickListener {
                requireActivity().backStack(R.id.nav_main)
            }
        }

        setTabLayoutFont()
    }

    private fun setTabLayoutFont() {
        val tabViewGroup = (binding.tabLayout.get(0) as ViewGroup)[0] as ViewGroup
        tabViewGroup.let {
            it.children.forEach { childView ->
                if(childView is TextView) {
                    val typeface = Typeface.createFromAsset(resources.assets, "nanum_square_round_b.ttf")
                    childView.typeface = typeface
                }
            }
        }
    }

    inner class SettingTabPagerAdapter : FragmentStateAdapter(this) {

        private val mFragmentList:MutableList<Fragment> = mutableListOf()

        init {
            mFragmentList.apply {
                add(SettingProfileFragment(arg.petId))
                add(SettingCharacterFragment(arg.petId))
                add(SettingFamilyFragment(arg.petId))
            }
        }

        override fun getItemCount() = mFragmentList.size
        override fun createFragment(position: Int) = mFragmentList[position]
    }
}