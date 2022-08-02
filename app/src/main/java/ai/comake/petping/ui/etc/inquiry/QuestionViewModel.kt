package ai.comake.petping.ui.etc.inquiry

import ai.comake.petping.Event
import ai.comake.petping.util.LogUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.appbar.AppBarLayout

/**
 * android-petping-2
 * Class: QuestionViewModel
 * Created by cliff on 2022/02/25.
 *
 * Description:
 */
class QuestionViewModel : ViewModel() {

    private val _isScroll = MutableLiveData<Boolean>().apply { value = false }
    val isScroll: LiveData<Boolean> get() = _isScroll

    val tabSelected = MutableLiveData<Event<Int>>()

    val appBarScrollListener = object : AppBarLayout.OnOffsetChangedListener {
        override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
            LogUtil.log("offset : $verticalOffset")
            if (verticalOffset == 0) {
                _isScroll.value = false
            } else {
                _isScroll.value = true
            }
        }
    }
}