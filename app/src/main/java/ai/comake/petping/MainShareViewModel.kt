package ai.comake.petping

import ai.comake.petping.AppConstants.AUTH_KEY
import ai.comake.petping.AppConstants.ID
import ai.comake.petping.api.Resource
import ai.comake.petping.data.db.badge.Badge
import ai.comake.petping.data.db.badge.BadgeRepository
import ai.comake.petping.data.repository.AppDataRepository
import ai.comake.petping.data.vo.MenuLink
import ai.comake.petping.util.LogUtil
import ai.comake.petping.util.SharedPreferencesManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    lateinit var badgeRepository: BadgeRepository

    @Inject
    lateinit var sharedPreferencesManager: SharedPreferencesManager

    @Inject
    lateinit var appDataRepository: AppDataRepository

    val showPopUp = MutableLiveData<Event<Boolean>>()
    val moveLinkedScreen = MutableLiveData<Event<MenuLink>>()
    val registFcmToken = MutableLiveData<Event<Boolean>>()
    var remoteBadge: Badge? = null
    var localBadge: Badge? = null

    val isSucceedBadge = MutableLiveData<Boolean>()

    var destinationScreen = "mainScreen"

//    fun setMenuLink(menu: MenuLink) {
//        menuLink.value = Event(menu)
//    }

    fun showPopUp(value: Boolean) {
        showPopUp.value = Event(value)
    }

    var menuLinkFcm = MenuLink.Fcm("", "")
    var menuLinkPetPing= MenuLink.PetPing("")

    fun getOnceFCMType(): String {
        return menuLinkFcm.type.also { menuLinkFcm.type = "" }
    }

    fun getOnceFCMLink(): String {
        return menuLinkFcm.link.also { menuLinkFcm.link = "" }
    }

    //로그인시 토큰 등록
    fun registFCMToken() {
        viewModelScope.launch(Dispatchers.IO) {
            val fcmToken = sharedPreferencesManager.getFCMTokenDataStore()
            LogUtil.log("TAG", "registFCMToken: $fcmToken")

            if (ID.isNotEmpty() && AppConstants.AUTH_KEY.isNotEmpty() && fcmToken.isNotEmpty()) {
                val body = makeFCMBody(AppConstants.ID, fcmToken)
                val response = appDataRepository.registFcmToken(AppConstants.AUTH_KEY, body)
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

    fun asyncNewBadge() {
        viewModelScope.launch() {
            val data = badgeRepository.select(0)
            localBadge = Badge(
                badgeSeq = 0,
                newMissionId = data?.newMissionId ?: 0,
                newSaveRewardId = data?.newSaveRewardId ?: 0,
                newReplyId = data?.newReplyId ?: 0,
                newNoticeId = data?.newNoticeId ?: 0,
                androidNewAppVersion = data?.androidNewAppVersion ?: BuildConfig.VERSION_NAME
            )

            val response = appDataRepository.getNewBadge(
                AUTH_KEY, ID)

            when (response) {
                is Resource.Success -> {
                    response.value.data?.let { data ->
                        remoteBadge = Badge(
                            badgeSeq = 0,
                            newMissionId = data.newMissionId ?: 0,
                            newSaveRewardId = data.newSaveRewardId ?: 0,
                            newReplyId = data.newReplyId ?: 0,
                            newNoticeId = data.newNoticeId ?: 0,
                            androidNewAppVersion = data.androidNewAppVersion
                                ?: BuildConfig.VERSION_NAME
                        )
                    }
                }
                is Resource.Failure -> {
                    Unit
                }
            }

            isSucceedBadge.postValue(true)

            LogUtil.log("TAG", "localBadge: $localBadge")
            LogUtil.log("TAG", "remoteBadge: $remoteBadge")
        }
    }
}