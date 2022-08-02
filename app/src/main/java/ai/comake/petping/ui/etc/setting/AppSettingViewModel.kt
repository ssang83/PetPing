package ai.comake.petping.ui.etc.setting

import ai.comake.petping.*
import ai.comake.petping.api.Resource
import ai.comake.petping.data.repository.AppDataRepository
import ai.comake.petping.data.vo.AppInfo
import android.view.View
import android.widget.CompoundButton
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.g00fy2.versioncompare.Version
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * android-petping-2
 * Class: AppSettingViewModel
 * Created by cliff on 2022/02/23.
 *
 * Description:
 */
@HiltViewModel
class AppSettingViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var appDataRepository: AppDataRepository

    private val _pushMarketingAlarm = MutableLiveData<Boolean>().apply { value = false }
    val pushMarketingAlarm: LiveData<Boolean> get() = _pushMarketingAlarm

    private val _rewardAlarm = MutableLiveData<Boolean>().apply { value = false }
    val rewardAlarm: LiveData<Boolean> get() = _rewardAlarm

    private val _eventAlarm = MutableLiveData<Boolean>().apply { value = false }
    val eventAlarm: LiveData<Boolean> get() = _eventAlarm

    private val _etcAlarm = MutableLiveData<Boolean>().apply { value = false }
    val etcAlarm: LiveData<Boolean> get() = _etcAlarm

    private val _walkAlarm = MutableLiveData<Boolean>().apply { value = false }
    val walkAlarm: LiveData<Boolean> get() = _walkAlarm

    private val _version = MutableLiveData<String>()
    val version: LiveData<String> get() = _version

    private val _isUpdate = MutableLiveData<Boolean>()
    val isUpdate: LiveData<Boolean> get() = _isUpdate

    private val _isScroll = MutableLiveData<Boolean>().apply { value = false }
    val isScroll:LiveData<Boolean> get() = _isScroll

    val moveToStore = MutableLiveData<Event<Unit>>()
    val moveToPolicy = MutableLiveData<Event<AppInfo>>()
    val moveToLincense = MutableLiveData<Event<String>>()
    val moveToBuisnessInfo = MutableLiveData<Event<String>>()
    val uiState = MutableLiveData<Event<UiState>>()

    var appInfo: AppInfo? = null

    val scrollChangeListener = object : View.OnScrollChangeListener {
        override fun onScrollChange(
            v: View?,
            scrollX: Int,
            scrollY: Int,
            oldScrollX: Int,
            oldScrollY: Int
        ) {
            if (v?.scrollY == 0) {
                _isScroll.value = false
            } else {
                _isScroll.value = true
            }
        }
    }

    fun loadData() {
        _version.value = "v${BuildConfig.VERSION_NAME}"
        uiState.emit(UiState.Loading)
        viewModelScope.launch {
            val response = appDataRepository.getAppInfo(AppConstants.AUTH_KEY, AppConstants.ID)
            when (response) {
                is Resource.Success -> {
                    uiState.emit(UiState.Success)
                    appInfo = response.value.data
                    if (appInfo?.pushMarketingInfo?.isNotEmpty() == true) {
                        _pushMarketingAlarm.value = appInfo?.pushMarketingInfo?.toBoolean()
                    }

                    val result = Version(BuildConfig.VERSION_NAME).isLowerThan(appInfo?.androidNewAppVersion)
                    _isUpdate.value = result
                }
                else -> uiState.emit(UiState.Failure(null))
            }
        }

        viewModelScope.launch {
            val response =
                appDataRepository.getNotificationStatus(AppConstants.AUTH_KEY, AppConstants.ID)
            when (response) {
                is Resource.Success -> {
                    uiState.emit(UiState.Success)
                    _rewardAlarm.value = response.value.data.reward
                    _etcAlarm.value = response.value.data.etc
                    _eventAlarm.value = response.value.data.event
                    _walkAlarm.value = response.value.data.walk
                }
                else -> uiState.emit(UiState.Failure(null))
            }
        }
    }

    fun onCheckedChanged(button: CompoundButton, isChecked: Boolean) {
        if (button.isPressed) {
            viewModelScope.launch {
                val body = makePushMarketingInfoBody(isChecked)
                val response = appDataRepository.setPushMarketingInfo(
                    AppConstants.AUTH_KEY,
                    AppConstants.ID,
                    body
                )
                if (response is Resource.Failure) {
                    loadData()
                }
            }
        }
    }

    fun onCheckedChanged(name: String, button: CompoundButton, isChecked: Boolean) {
        if (button.isPressed) {
            viewModelScope.launch {
                val body = makeNotificationStatusBody(isChecked)
                val response =
                    appDataRepository.setNotificationStatus(AppConstants.AUTH_KEY, name, body)
                if (response is Resource.Failure) {
                    loadData()
                }
            }
        }
    }

    fun goToPlayStore() {
        moveToStore.emit()
    }

    fun goToPolicy(appInfo: AppInfo) {
        moveToPolicy.emit(appInfo)
    }

    fun goToOpenSourceLicense(url: String) {
        moveToLincense.emit(url)
    }

    fun goToBusinessInfo(url: String) {
        moveToBuisnessInfo.emit(url)
    }
}