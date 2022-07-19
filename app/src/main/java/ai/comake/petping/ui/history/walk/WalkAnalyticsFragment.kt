package ai.comake.petping.ui.history.walk

import ai.comake.petping.data.vo.WalkStatsData
import ai.comake.petping.databinding.FragmentWalkAnalyticsBinding
import ai.comake.petping.observeEvent
import ai.comake.petping.ui.base.BaseFragment
import ai.comake.petping.util.setSafeOnClickListener
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import dagger.hilt.android.AndroidEntryPoint

/**
 * android-petping-2
 * Class: WalkAnalyticsFragment
 * Created by cliff on 2022/02/24.
 *
 * Description:
 */
@AndroidEntryPoint
class WalkAnalyticsFragment(
    private val petId:Int
) : BaseFragment<FragmentWalkAnalyticsBinding>(FragmentWalkAnalyticsBinding::inflate) {

    private val viewModel: WalkAnalyticsViewModel by viewModels()

    var walkStat: WalkStatsData? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        with(viewModel) {

            loadData(petId)

            setUpGraphData.observeEvent(viewLifecycleOwner) { walkStatsData ->
                setGraph(walkStatsData)
            }
        }
    }

    private fun setGraph(_walkStats: WalkStatsData) {
        walkStat = _walkStats

        val graphData = arrayListOf<Int>()
        walkStat!!.barGraphData.values.forEach {
            graphData.add(it.toInt())
        }

        with(binding) {
            simpleGraph.apply {
                setData(graphData)
                setHighlight(11)
                setMonth(walkStat!!.barGraphData.monthlyCount)
            }

            viewPager.adapter = ScreenSlidePagerAdapter(requireActivity())
            viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    simpleGraph.setHighlight(position)
                    when (position) {
                        0 -> {
                            pagerLeft.isEnabled = false
                            pagerRight.isEnabled = true
                        }
                        (walkStat!!.walkStats.size) - 1 -> {
                            pagerRight.isEnabled = false
                            pagerLeft.isEnabled = true
                        }
                        else -> {
                            pagerRight.isEnabled = true
                            pagerLeft.isEnabled = true
                        }
                    }
                }
            })

            viewPager.setCurrentItem(walkStat!!.walkStats.size, false)

            pagerLeft.setSafeOnClickListener {
                viewPager.setCurrentItem(
                    viewPager.currentItem - 1,
                    true
                )
            }

            pagerRight.setSafeOnClickListener {
                viewPager.setCurrentItem(
                    viewPager.currentItem + 1,
                    true
                )
            }
        }
    }

    private inner class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = walkStat?.walkStats?.size?:0

        override fun createFragment(position: Int): Fragment =
            WeeklyReportFragment(walkStat?.walkStats?.get(position))
    }
}