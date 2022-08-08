package ai.comake.petping.ui.common.widget

import ai.comake.petping.R
import ai.comake.petping.data.vo.ShopPopup
import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.viewpager2.widget.ViewPager2


/**
 * android-petping-2
 * Class: OnPageChangeCallbackForInfiniteIndicator
 * Created by cliff on 2022/08/02.
 *
 * Description:
 */
class OnPageChangeCallbackForInfiniteIndicator(
    _activity: Activity,
    _bannerList: List<ShopPopup>,
    _currentItem: Int
) : ViewPager2.OnPageChangeCallback() {

    private val activity: Activity = _activity
    private val pageIndicatorList: MutableList<ImageView> = mutableListOf()
    private val bannerList: List<ShopPopup> = _bannerList
    private var containerIndicator: LinearLayout? = null
    private var viewPagerActivePosition = _currentItem
    private var positionToUse = 0
    private var actualPosition = _currentItem

    init {
        loadIndicators()
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        actualPosition = position
        val positionToUseOld = positionToUse
        positionToUse = if (actualPosition < viewPagerActivePosition && positionOffset < 0.5f) {
            actualPosition % bannerList.size
        } else {
            if (positionOffset > 0.5f) {
                (actualPosition + 1) % bannerList.size
            } else {
                actualPosition % bannerList.size
            }
        }
        if (positionToUseOld != positionToUse) {
            loadIndicators()
        }
    }

    override fun onPageScrollStateChanged(state: Int) {
        if (state == 0) {
            viewPagerActivePosition = actualPosition
            positionToUse = viewPagerActivePosition % bannerList.size
            loadIndicators();
        }
    }

    override fun onPageSelected(position: Int) {}

    private fun loadIndicators() {
        containerIndicator = activity.findViewById<View>(R.id.container_indicator) as LinearLayout
        if (pageIndicatorList.size < 1) {
            for (banner in bannerList) {
                val imageView = ImageView(activity)
                imageView.setImageResource(R.drawable.default_indicator_dot_2) // normal indicator image
                imageView.scaleType = ImageView.ScaleType.CENTER_INSIDE
                imageView.layoutParams = ViewGroup.LayoutParams(
                    activity.resources.getDimensionPixelOffset(R.dimen.shop_banner_indicator_width),
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                pageIndicatorList.add(imageView)
            }
        }

        containerIndicator?.removeAllViews()

        for (x in pageIndicatorList.indices) {
            val imageView = pageIndicatorList[x]
            imageView.setImageResource(if (x == positionToUse) R.drawable.selected_indicator_dot_2 else R.drawable.default_indicator_dot_2) // active and notactive indicator
            containerIndicator?.addView(imageView)
        }
    }
}