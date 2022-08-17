package ai.comake.petping.ui.etc.inquiry

import ai.comake.petping.R
import ai.comake.petping.databinding.FragmentQuestionBinding
import ai.comake.petping.observeEvent
import ai.comake.petping.ui.base.BaseFragment
import ai.comake.petping.util.backStack
import ai.comake.petping.util.setSafeOnClickListener
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
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator

/**
 * android-petping-2
 * Class: QuestionFragment
 * Created by cliff on 2022/02/22.
 *
 * Description:
 */
class QuestionFragment : BaseFragment<FragmentQuestionBinding>(FragmentQuestionBinding::inflate) {

    private val viewModel : QuestionViewModel by viewModels()

    private lateinit var pagerAdapter: QuestionTabPagerAdapter

    lateinit var instance:QuestionFragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        instance = this

        viewModel.tabSelected.observeEvent(viewLifecycleOwner) { position ->
            binding.viewPagerContainer.setCurrentItem(position, false)
        }

        with(binding) {
            viewPagerContainer.apply {
                pagerAdapter = QuestionTabPagerAdapter()
                adapter = pagerAdapter
                isUserInputEnabled = false
            }

            TabLayoutMediator(tabLayout, viewPagerContainer) { tab, position ->
                when (position) {
                    0 -> tab.text = getString(R.string.inquiry_tab)
                    else -> tab.text = getString(R.string.faq)
                }
            }.attach()

            header.btnBack.setSafeOnClickListener {
                requireActivity().backStack(R.id.nav_main)
            }
        }

        setTabLayoutFont()
    }

    fun scrollTop() {
        binding.appBarHeader.setExpanded(true, true)
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

    inner class QuestionTabPagerAdapter : FragmentStateAdapter(this) {

        private val mFragmentList:MutableList<Fragment> = mutableListOf()

        init {
            mFragmentList.apply {
                add(InquiryDoFragment())
                add(FAQFragment())
            }
        }

        override fun getItemCount() = mFragmentList.size
        override fun createFragment(position: Int) = mFragmentList[position]
    }
}