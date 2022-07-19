package ai.comake.petping.ui.home

import ai.comake.petping.Event
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/*
 프래그먼트간의 이벤트 전달(공유 ViewModel)
 */
class HomeShareViewModel : ViewModel() {
    private val _isVisibleBottomNavigation = MutableLiveData<Event<Boolean>>()
    val isVisibleBottomNavigation: LiveData<Event<Boolean>> = _isVisibleBottomNavigation

    fun isVisibleBottomNavigation(value: Boolean) {
        _isVisibleBottomNavigation.value = Event(value)
    }
}