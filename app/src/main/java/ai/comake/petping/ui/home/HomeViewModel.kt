package ai.comake.petping.ui.home

import ai.comake.petping.AppConstants
import ai.comake.petping.AppConstants.AUTH_KEY
import ai.comake.petping.AppConstants.ID
import ai.comake.petping.BuildConfig
import ai.comake.petping.api.Resource
import ai.comake.petping.data.db.badge.Badge
import ai.comake.petping.data.repository.AppDataRepository
import ai.comake.petping.ui.home.walk.service.LocationUpdatesService
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {
    val isStartWalk = LocationUpdatesService._isStartWalk.asLiveData()
    @Inject
    lateinit var appDataRepository: AppDataRepository

//    fun asyncBadge() = viewModelScope.launch {
//        try {
//            val response = safeApiCall {
//                apiService.requestBadgeInfos(
//                    AppConstants.AUTH_KEY,
//                    AppConstants.ID
//                )
//            }
//
//            when (response) {
//                is ResultWrapper.Success -> {
//                    response.value.data?.let { data ->
//                        badge = Badge(
//                            badgeSeq = 0,
//                            newMissionId = data.newMissionId ?: 0,
//                            newSaveRewardId = data.newSaveRewardId ?: 0,
//                            newReplyId = data.newReplyId ?: 0,
//                            newNoticeId = data.newNoticeId ?: 0,
//                            androidNewAppVersion = data.androidNewAppVersion
//                                ?: BuildConfig.VERSION_NAME
//                        )
//                    }
//                }
//                is ResultWrapper.GenericError -> {
//                    response.error?.let { error ->
//                        Log.d("error code : ${error.code}, error message : ${error.message}")
//                    }
//                }
//                is ResultWrapper.NetworkError -> Log.d("network error")
//            }
//        } catch (e: Exception) {
//            Log.p(e)
//        }
//    }



}