package ai.comake.petping.ui.home

import ai.comake.petping.Event
import ai.comake.petping.emit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/*
 프래그먼트간의 이벤트 전달(공유 ViewModel)
 */
class HomeShareViewModel : ViewModel() {
    val isVisibleBottomNavigation = MutableLiveData<Event<Boolean>>()

    private val _moveToWalk = MutableLiveData<Event<Unit>>()
    val moveToWalk: LiveData<Event<Unit>> = _moveToWalk

    private val _moveToReward = MutableLiveData<Event<Unit>>()
    val moveToReward: LiveData<Event<Unit>> = _moveToReward

    var screenName: String = ""

    val isSucceedBadge = MutableLiveData<Event<Boolean>>()

//    fun isVisibleBottomNavigation(value: Boolean) {
//        _isVisibleBottomNavigation.value = Event(value)
//    }

    fun goToWalk() {
        _moveToWalk.emit()
    }

    fun goToReward() {
        _moveToReward.emit()
    }
}