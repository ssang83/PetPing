package ai.comake.petping.ui.profile

import ai.comake.petping.R
import ai.comake.petping.databinding.FragmentDogProfileBinding
import ai.comake.petping.observeEvent
import ai.comake.petping.ui.base.BaseFragment
import ai.comake.petping.ui.history.walk.WalkAnalyticsFragment
import ai.comake.petping.ui.history.walk.WalkHistoryFragment
import ai.comake.petping.util.backStack
import ai.comake.petping.util.setSafeOnClickListener
import ai.comake.petping.util.updateBlackStatusBar
import ai.comake.petping.util.updateWhiteStatusBar
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
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
 * Class: DogProfileFragment
 * Created by cliff on 2022/02/24.
 *
 * Description:
 */
@AndroidEntryPoint
class DogProfileFragment :
    BaseFragment<FragmentDogProfileBinding>(FragmentDogProfileBinding::inflate) {

    private val viewModel: DogProfileViewModel by viewModels()
    private val args:DogProfileFragmentArgs by navArgs()

    private lateinit var pagerAdapter: TabPagerAdapter
    lateinit var instance:DogProfileFragment

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (viewModel.needPopup.value == true) {
                viewModel.closePopupImage()
            } else {
                requireActivity().backStack(R.id.nav_main)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instance = this
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateWhiteStatusBar(requireActivity().window)
        requireActivity().onBackPressedDispatcher.addCallback(onBackPressedCallback)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        with(viewModel) {

            loadPetInfo(args.config.petId, args.config.viewMode)

            moveToSetting.observeEvent(viewLifecycleOwner) { petId ->
                requireActivity().findNavController(R.id.nav_main).navigate(
                    DogProfileFragmentDirections.actionDogProfileFragmentToSettingFragment(petId)
                )
            }

            updateStatusBar.observeEvent(viewLifecycleOwner) { color ->
                when(color) {
                    "black" -> updateBlackStatusBar(requireActivity().window)
                    else -> updateWhiteStatusBar(requireActivity().window)
                }
            }

            tabSelected.observeEvent(viewLifecycleOwner) { position ->
                if(position == 0) scrollTop()
                binding.viewPagerContainer.setCurrentItem(position, false)
            }
        }

        setUpUi()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        onBackPressedCallback.remove()
    }

    private fun setUpUi() {
        with(binding) {

            viewPagerContainer.apply {
                pagerAdapter = TabPagerAdapter()
                adapter = pagerAdapter
                isUserInputEnabled = false
            }

            TabLayoutMediator(tabLayout, viewPagerContainer) { tab, position ->
                when (position) {
                    0 -> tab.text = getString(R.string.walk_history)
                    else -> tab.text = getString(R.string.walk_analytics)
                }
            }.attach()

            btnBack.setSafeOnClickListener {
                if (viewModel?.needPopup?.value == true) {
                    viewModel?.closePopupImage()
                } else {
                    requireActivity().backStack(R.id.nav_main)
                }
            }
        }

        setTabFont()
    }

    fun scrollTop() {
        binding.appBar.setExpanded(true, true)
    }

    fun showFullImage(imgUrl:String) {
        viewModel.showPopupImage(imgUrl)
    }

    private fun setTabFont() {
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

    inner class TabPagerAdapter : FragmentStateAdapter(this) {

        private val mFragmentList:MutableList<Fragment> = mutableListOf()

        init {
            mFragmentList.apply {
                add(WalkHistoryFragment(args.config.petId, args.config.viewMode))
                add(WalkAnalyticsFragment(args.config.petId))
            }
        }

        override fun getItemCount() = mFragmentList.size
        override fun createFragment(position: Int) = mFragmentList[position]
    }
}