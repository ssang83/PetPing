package ai.comake.petping

import ai.comake.petping.data.vo.MenuLink
import ai.comake.petping.ui.home.dashboard.DashboardFragment
import ai.comake.petping.ui.home.walk.WalkFragment
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

/*
 프래그먼트와 액티비티의 이벤트 전달(공유 ViewModel)
 */
class MainShareViewModel : ViewModel() {
    val showPopUp = MutableLiveData<Event<Boolean>>()
    val menuLink = MutableLiveData<Event<MenuLink>>()

    fun setMenuLink(menu: MenuLink) {
        menuLink.value = Event(menu)
    }

    fun showPopUp(value: Boolean) {
        showPopUp.value = Event(value)
    }
}