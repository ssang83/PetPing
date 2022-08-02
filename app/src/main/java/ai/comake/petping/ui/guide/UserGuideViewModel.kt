package ai.comake.petping.ui.guide

import ai.comake.petping.Event
import ai.comake.petping.emit
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.viewpager2.widget.ViewPager2

/**
 * android-petping-2
 * Class: UserGuideViewModel
 * Created by cliff on 2022/07/28.
 *
 * Description:
 */

class UserGuideViewModel : ViewModel() {

    private val GUIDE_PAGE_COUNT = 5

    val lastPage = MutableLiveData<Boolean>().apply { value = false }
    val pagePosition = MutableLiveData<Int>().apply { value = -1 }

    val goToStart = MutableLiveData<Event<Unit>>()
    val nextPage = MutableLiveData<Event<Int>>()

    var pos = -1

    val pageChangeListener = object : ViewPager2.OnPageChangeCallback(){
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            pos = position
            pagePosition.value = position
            lastPage.value = if(position == GUIDE_PAGE_COUNT - 1) {
                true
            } else {
                false
            }
        }
    }

    fun start() {
        goToStart.emit()
    }

    fun next() {
        nextPage.emit(pos)
    }
}