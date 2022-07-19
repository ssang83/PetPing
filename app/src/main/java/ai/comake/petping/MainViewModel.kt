package ai.comake.petping

import ai.comake.petping.api.Resource
import ai.comake.petping.data.repository.AppDataRepository
import ai.comake.petping.data.vo.AppVersionResponse
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

    private val _processing = MutableStateFlow(false)
    val processing = _processing.asStateFlow()

    private val _openHomeScreen = MutableLiveData<Event<Boolean>>()
    val openHomeScreen: LiveData<Event<Boolean>> = _openHomeScreen

    private var _appData = MutableStateFlow<AppVersionResponse.Data.AppUpdateVersion?>(null)
    val appData = _appData.asStateFlow()

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

    fun testLogin() = viewModelScope.launch {
        val body = makeTestLoginBody()
        val response = appDataRepository.testLogin(SAPA_KEY, body)
        when (response) {
            is Resource.Success -> {
                AppConstants.ID = response.value.data.id
                AppConstants.AUTH_KEY = "Bearer ${response.value.data.authorizationToken}"
                AppConstants.LOGIN_HEADER_IS_VISIBLE = false
                _openHomeScreen.emit(true)
            }
            is Resource.Failure -> AppConstants.LOGIN_HEADER_IS_VISIBLE = true
        }
    }
}