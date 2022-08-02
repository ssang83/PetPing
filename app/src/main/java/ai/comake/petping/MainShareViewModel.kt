package ai.comake.petping

import ai.comake.petping.api.Resource
import ai.comake.petping.data.repository.AppDataRepository
import ai.comake.petping.data.vo.MenuLink
import ai.comake.petping.util.LogUtil
import ai.comake.petping.util.SharedPreferencesManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/*
 프래그먼트와 액티비티의 이벤트 전달(공유 ViewModel)
 */
@HiltViewModel
class MainShareViewModel @Inject constructor() : ViewModel() {
    @Inject
    lateinit var sharedPreferencesManager: SharedPreferencesManager
    @Inject
    lateinit var appDataRepository: AppDataRepository

    val showPopUp = MutableLiveData<Event<Boolean>>()
    val menuLink = MutableLiveData<Event<MenuLink>>()
    val registFcmToken = MutableLiveData<Event<Boolean>>()

    fun setMenuLink(menu: MenuLink) {
        menuLink.value = Event(menu)
    }

    fun showPopUp(value: Boolean) {
        showPopUp.value = Event(value)
    }

    //로그인시 토큰 등록
    fun registFCMToken(){
        viewModelScope.launch(Dispatchers.IO) {
            val fcmToken = sharedPreferencesManager.getFCMTokenDataStore()
            LogUtil.log("TAG", "registFCMToken: $fcmToken")

            if(AppConstants.ID.isNotEmpty() && AppConstants.AUTH_KEY.isNotEmpty() && fcmToken.isNotEmpty()){
                val body = makeFCMBody(AppConstants.ID, fcmToken)
                val response = appDataRepository.registFcmToken(AppConstants.AUTH_KEY,body)
                when (response) {
                    is Resource.Success -> {
                        LogUtil.log("TAG", "response: ${response.value}")
                    }
                    else -> {
                        //Do Nothing
                    }
                }
            }
        }
    }
}