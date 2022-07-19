package ai.comake.petping.ui.etc.member_info.withdrawal

import ai.comake.petping.*
import ai.comake.petping.api.Resource
import ai.comake.petping.data.repository.UserDataRepository
import ai.comake.petping.data.vo.LeaveType
import android.view.MotionEvent
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.ab180.airbridge.Airbridge
import co.ab180.airbridge.event.StandardEventCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * android-petping-2
 * Class: WithdrawalViewModel
 * Created by cliff on 2022/03/18.
 *
 * Description:
 */
@HiltViewModel
class WithdrawalViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var userDataRepository: UserDataRepository

    val otherReason = MutableLiveData<String>().apply { value = "" }
    val reason = MutableLiveData<String>().apply { value = "" }
    val etc = MutableLiveData<Boolean>().apply { value = false }

    val withdrawalPopup = MutableLiveData<Event<Unit>>()
    val moveToHome = MutableLiveData<Event<Unit>>()
    val uiState = MutableLiveData<Event<UiState>>()
    val selectReason = MutableLiveData<Event<Unit>>()

    var leaveType = mutableListOf<String>()
    var leaveReason = listOf<LeaveType>()
    var type = ""
    var other = ""

    val touchListener = View.OnTouchListener { v, event ->
        if (event.action == MotionEvent.ACTION_UP) {
            selectReason.emit()
        }
        true
    }

    fun loadData() = viewModelScope.launch {
        val response = userDataRepository.getLeaveReason(AppConstants.AUTH_KEY)
        when (response) {
            is Resource.Success -> {
                leaveReason = response.value.data
                leaveReason.forEach { leaveType.add(it.leaveTypeStr) }
            }
        }
    }

    fun withdrawal(_type: String, _otherReason: String) {
        type = _type
        other = _otherReason
        withdrawalPopup.emit()
    }

    fun goToWithdrawal() = viewModelScope.launch {
        uiState.emit(UiState.Loading)
        val body = makeWithdrawalBody(
            leaveReason.find { it.leaveTypeStr == type }?.leaveType ?: 0,
            other
        )
        val response = userDataRepository.withdrawal(AppConstants.AUTH_KEY, AppConstants.ID, body)
        when (response) {
            is Resource.Success -> {
                uiState.emit(UiState.Success)
                // airbridge logout event
                val event = co.ab180.airbridge.event.Event(StandardEventCategory.SIGN_OUT)
                Airbridge.trackEvent(event)
                Airbridge.expireUser()

                AppConstants.LOGIN_HEADER_IS_VISIBLE = true
                AppConstants.PROFILE_HEADER_IS_VISIBLE = true
                AppConstants.AUTH_KEY = ""
                AppConstants.ID = ""
                moveToHome.emit()
            }
            else -> uiState.emit(UiState.Failure(null))
        }
    }
}