package ai.comake.petping

import ai.comake.petping.AppConstants.SAPA_KEY
import ai.comake.petping.api.Resource
import ai.comake.petping.data.repository.AppDataRepository
import ai.comake.petping.data.repository.WalkRepository
import ai.comake.petping.data.vo.AppVersionResponse
import ai.comake.petping.data.vo.WalkFinish
import ai.comake.petping.data.vo.WalkFinishRequest
import ai.comake.petping.google.database.room.walk.WalkDBRepository
import ai.comake.petping.util.Coroutines
import ai.comake.petping.util.LogUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel(
) {
    @Inject
    lateinit var appDataRepository: AppDataRepository

    @Inject
    lateinit var walkRepository: WalkRepository

    @Inject
    lateinit var walkDBRepository: WalkDBRepository

    private val _processing = MutableStateFlow(false)
    val processing = _processing.asStateFlow()

    private val _openHomeScreen = MutableLiveData<Event<Boolean>>()
    val openHomeScreen: LiveData<Event<Boolean>> = _openHomeScreen

    private var _appData = MutableStateFlow<AppVersionResponse.Data.AppUpdateVersion?>(null)
    val appData = _appData.asStateFlow()

    val systemCheckTitle = MutableStateFlow("")
    val systemCheckDesc = MutableStateFlow("")
    val systemCheckMode = MutableStateFlow(false)

    // Event
    private val _eventFlow = MutableEventFlow<MainEvent>()
    val eventFlow = _eventFlow.asEventFlow()

    private val _isSucceedWalkFinish = MutableLiveData<Event<WalkFinish>>()
    val isSucceedWalkFinish: LiveData<Event<WalkFinish>>
        get() = _isSucceedWalkFinish

    private val _isFailedWalkFinish = MutableLiveData<Event<Boolean>>()
    val isFailedWalkFinish: LiveData<Event<Boolean>>
        get() = _isFailedWalkFinish

    //    private val _openWidzet = MutableStateFlow<Event<Boolean>>(Event(false))
//    val openWidzet: StateFlow<Event<Boolean>> = _openWidzet

    fun loadAppVersion() {
        viewModelScope.launch {
            try {
                _processing.value = true
                val response = appDataRepository.appVersion(SAPA_KEY)
                when (response) {
                    is Resource.Success -> {
//                        _openHomeScreen.emit(true)
                    }
                    is Resource.Failure -> {
                    }
                }
            } catch (e: Exception) {
            } finally {
                _processing.value = false
            }
        }
    }

//    fun testLogin() = viewModelScope.launch {
//        LogUtil.log("TAG", ": $")
//        val body = makeTestLoginBody()
//        val response = appDataRepository.testLogin(SAPA_KEY, body)
//        when (response) {
//            is Resource.Success -> {
//                AppConstants.ID = response.value.data.id
//                AppConstants.AUTH_KEY = "Bearer ${response.value.data.authorizationToken}"
//                AppConstants.LOGIN_HEADER_IS_VISIBLE = false
//                _openHomeScreen.emit(true)
//            }
//            is Resource.Failure -> AppConstants.LOGIN_HEADER_IS_VISIBLE = true
//        }
//    }

    fun checkAppVersion() = Coroutines.main(this) {
        val response = appDataRepository.getAppVersion(SAPA_KEY)
        when (response) {
            is Resource.Success -> {
                val data = response.value.data
                val selectVersion =
                    data.appUpdateVersion.selectUpdateVerAos.replace("[^0-9]".toRegex(), "").toInt()
                val forceVersion =
                    data.appUpdateVersion.forceUpdateVerAos.replace("[^0-9]".toRegex(), "").toInt()
                val version = BuildConfig.VERSION_NAME.replace("[^0-9]".toRegex(), "").toInt()
                LogUtil.log("selectVersion : $selectVersion, forceVersion : $forceVersion")

                val isForcedUpdate = version <= forceVersion
                val isSelectUpdate = version <= selectVersion
                if (isForcedUpdate) {
                    event(MainEvent.ForceUpdate)
                } else if (isSelectUpdate) {
                    event(MainEvent.SelectUpdate)
                } else {
                    event(MainEvent.SystemCheck)
                }
            }
        }
    }

    suspend fun asyncWalkFinish(authKey: String, walkId: Int, body: WalkFinishRequest) {
        LogUtil.log("TAG", ": $")
        val response = walkRepository.finishWalk(
            authKey,
            walkId,
            body
        )

        when (response) {
            is Resource.Success -> {
                walkDBRepository.deleteAll()
                _isSucceedWalkFinish.postValue(Event(response.value.data))
            }
            else -> {
                _isFailedWalkFinish.postValue(Event(false))
            }
        }
    }

    private suspend fun event(event: MainEvent) {
        _eventFlow.emit(event)
    }

    sealed class MainEvent {
        object ForceUpdate : MainEvent()
        object SelectUpdate : MainEvent()
        object SystemCheck : MainEvent()
    }
}