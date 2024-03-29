package ai.comake.petping.ui.home.dashboard

import ai.comake.petping.AppConstants
import ai.comake.petping.BuildConfig
import ai.comake.petping.Event
import ai.comake.petping.api.Resource
import ai.comake.petping.data.repository.AppDataRepository
import ai.comake.petping.data.repository.PetRepository
import ai.comake.petping.data.vo.*
import ai.comake.petping.emit
import ai.comake.petping.util.Coroutines
import ai.comake.petping.util.LogUtil
import ai.comake.petping.util.SharedPreferencesManager
import ai.comake.petping.util.encrypt
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import co.ab180.airbridge.Airbridge
import co.ab180.airbridge.event.model.SemanticAttributes
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import java.time.LocalDate
import java.util.*
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor() : ViewModel() {
    @Inject
    lateinit var petRepository: PetRepository

    @Inject
    lateinit var dashboardRepo: AppDataRepository

    @Inject
    lateinit var preferences: SharedPreferencesManager

    private val _mutableStateFlow = MutableStateFlow(false)
    val mutableStateFlow = _mutableStateFlow.asStateFlow()

    private val _petImage = MutableLiveData<String>()
    val petImage:LiveData<String> get() = _petImage

    private val _isScroll = MutableLiveData<Boolean>().apply { value = false }
    val isScroll:LiveData<Boolean> get() = _isScroll

    private val _isBadge = MutableLiveData<Boolean>().apply { value = true }
    val isBadge:LiveData<Boolean> get() = _isBadge

    private val _petMessage = MutableLiveData<String>()
    val petMessage:LiveData<String> get() = _petMessage

    private val _walkDayCount = MutableLiveData(0)
    val walkDayCount:LiveData<Int> get() = _walkDayCount

    private val _walkTotalTime = MutableLiveData<String>()
    val walkTotalTime:LiveData<String> get() = _walkTotalTime

    private val _walkTotalDistance = MutableLiveData<String>()
    val walkTotalDistance:LiveData<String> get() = _walkTotalDistance

    private val _walkStatsType = MutableLiveData<String>()
    val walkStatsType: LiveData<String> get() = _walkStatsType

    private val _cityName = MutableLiveData<String>()
    val cityName: LiveData<String> get() = _cityName

    private val _walkIndex = MutableLiveData<String>()
    val walkIndex: LiveData<String> get() = _walkIndex

    private val _walkIndexDay = MutableLiveData<String>()
    val walkIndexDay: LiveData<String> get() = _walkIndexDay

    private val _petName = MutableLiveData<String>()
    val petName: LiveData<String> get() = _petName

    private val _isWalkablePet = MutableLiveData(false)
    val isWalkablePet: LiveData<Boolean> get() = _isWalkablePet

    private val _invitationWalk = MutableLiveData(false)
    val invitationWalk: LiveData<Boolean> get() = _invitationWalk

    private val _walkablePetSetting = MutableLiveData(false)
    val walkablePetSetting: LiveData<Boolean> get() = _walkablePetSetting

    private val _walkRecommend = MutableLiveData(false)
    val walkRecommend: LiveData<Boolean> get() = _walkRecommend

    private val _tipList = MutableLiveData<List<Tip>>()
    val tipList:LiveData<List<Tip>> get() = _tipList

    private val _missionAlert = MutableLiveData<RewardMission?>()
    val missionAlert: LiveData<RewardMission?> get() = _missionAlert

    private val _missionPetAlert = MutableLiveData<MissionPetSettingAlert?>()
    val missionPetAlert:LiveData<MissionPetSettingAlert?> get() = _missionPetAlert

    private val _closeMissionAlert = MutableLiveData<Boolean>().apply { value = false }
    val closeMissionAlert:LiveData<Boolean> get() = _closeMissionAlert

    private val _expandedAlert = MutableLiveData<Boolean>().apply { value = false }
    val expandedAlert:LiveData<Boolean> get() = _expandedAlert

    private val _expandedMissionPetAlert = MutableLiveData<Boolean>().apply { value = false }
    val expandedMissionPetAlert:LiveData<Boolean> get() = _expandedMissionPetAlert

    private val _closeMissionPetAlert = MutableLiveData<Boolean>().apply { value = false }
    val closeMissionPetAlert:LiveData<Boolean> get() = _closeMissionPetAlert

    private val _friendInfoList: MutableLiveData<List<PingZoneMeetPet>> = MutableLiveData()
    val friendInfoList:LiveData<List<PingZoneMeetPet>> get() = _friendInfoList

    private val _friendName = MutableLiveData<String>().apply { value = "" }
    val friendName:LiveData<String> get() = _friendName

    private val _friendDescription = MutableLiveData<String>().apply { value = "" }
    val friendDescription:LiveData<String> get() = _friendDescription

    private val _friendProfile = MutableLiveData<String>().apply { value = "" }
    val friendProfile:LiveData<String> get() = _friendProfile

    // Event
    private val _moveToEtc = MutableSharedFlow<Unit>()
    val moveToEtc:SharedFlow<Unit> get() = _moveToEtc.asSharedFlow()

    private val _showProfilePopup = MutableLiveData<Event<Unit>>()
    val showProfilePopup:LiveData<Event<Unit>> get() = _showProfilePopup

    private val _petListSuccess = MutableLiveData<Event<Unit>>()
    val petListSuccess:LiveData<Event<Unit>> get() = _petListSuccess

    private val _moveToPingTipAll = MutableLiveData<Event<Unit>>()
    val moveToPingTipAll:LiveData<Event<Unit>> get() = _moveToPingTipAll

    private val _updateAnimation = MutableLiveData<Event<DashboardAnimationInfo>>()
    val updateAnimation:LiveData<Event<DashboardAnimationInfo>> get() = _updateAnimation

    private val _moveToPingTip= MutableLiveData<Event<String>>()
    val moveToPingTip:LiveData<Event<String>> get() = _moveToPingTip

    private val _moveToMakeProfile = MutableLiveData<Event<Unit>>()
    val moveToMakeProfile:LiveData<Event<Unit>> get() = _moveToMakeProfile

    private val _moveToProfile= MutableLiveData<Event<Int>>()
    val moveToProfile:LiveData<Event<Int>> get() = _moveToProfile

    private val _moveToWalkHistory= MutableLiveData<Event<WalkHistory>>()
    val moveToWalkHistory:LiveData<Event<WalkHistory>> get() = _moveToWalkHistory

    private val _moveToMissionPet= MutableLiveData<Event<Unit>>()
    val moveToMissionPet:LiveData<Event<Unit>> get() = _moveToMissionPet

    private val _moveToWalk= MutableLiveData<Event<Unit>>()
    val moveToWalk:LiveData<Event<Unit>> get() = _moveToWalk

    private val _moveToMission= MutableLiveData<Event<Unit>>()
    val moveToMission:LiveData<Event<Unit>> get() = _moveToMission

    private val _initialProfile = MutableLiveData<Event<Unit>>()
    val initialProfile:LiveData<Event<Unit>> get() = _initialProfile

    val profileList = MutableLiveData<List<Pet>>()
    val showHomePopup = MutableLiveData<Event<List<Popup>>>()

    var speechBubbleUrl = ""
    var petInfoList: List<DashboardPet> = listOf()

    var petId : Int? = null
    var petList: List<Pet> = listOf()

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

    fun goToMyPage() {
        Coroutines.main(this) { _moveToEtc.emit(Unit) }
    }

    fun showProfilePopup() {
        _showProfilePopup.emit()
    }

    fun goToPinTipAll() {
        _moveToPingTipAll.emit()
    }

    fun goToPingTip(url:String) {
        _moveToPingTip.emit(url)
    }

    fun goToMakeProfile() {
        _moveToMakeProfile.emit()
    }

    fun goToPetProfile(id: Int) {
        _moveToProfile.emit(id)
    }

    fun goToWalkHistory(data:WalkHistory) {
        _moveToWalkHistory.emit(data)
    }

    fun getPetList() = Coroutines.main(this) {
        val response = petRepository.getPetList(AppConstants.AUTH_KEY, AppConstants.ID)
        when (response) {
            is Resource.Success -> {
                profileList.value = response.value.data.pets
                petList = response.value.data.pets
                _petListSuccess.emit()
            }
            is Resource.Error -> {
                response.errorBody?.let { error ->
                    if (error.code == "C2070") {
                        _initialProfile.emit()
                    }
                }
            }
        }
    }

    fun getDashboard(context: Context, lat: Double, lng: Double) {
        Coroutines.main(this) {
            val response = dashboardRepo.getDashboard(
                AppConstants.AUTH_KEY,
                BuildConfig.VERSION_NAME,
                Build.VERSION.RELEASE,
                Build.MODEL,
                "temp",
                UUID.randomUUID().toString(),
                lat.encrypt(),
                lng.encrypt()
            )

            when (response) {
                is Resource.Success -> {
                    processDashboardData(context, response.value.data, lat, lng, true)
                }
            }
        }
    }

    fun getDashboard(context: Context, profileId:Int) = Coroutines.main(this) {
        val response = dashboardRepo.getDashboard(
            AppConstants.AUTH_KEY,
            BuildConfig.VERSION_NAME,
            Build.VERSION.RELEASE,
            Build.MODEL,
            "temp",
            UUID.randomUUID().toString(),
            profileId,
            AppConstants.lastLocation.latitude.encrypt(),
            AppConstants.lastLocation.longitude.encrypt()
        )

        when (response) {
            is Resource.Success -> {
                processDashboardData(
                    context,
                    response.value.data,
                    AppConstants.lastLocation.latitude,
                    AppConstants.lastLocation.longitude,
                    true
                )
            }
        }
    }

    fun getPingTip() = Coroutines.io(this) {
        val response = dashboardRepo.getPingTip(AppConstants.AUTH_KEY,1)
        when(response) {
            is Resource.Success -> {
                _tipList.postValue(response.value.data.boardBannerList)
            }
        }
    }

    fun closeAlert() {
        _closeMissionAlert.value = true
        AppConstants.closeMissionAlert = true
    }

    fun closeExpandedAlert() {
        _closeMissionAlert.value = true
        AppConstants.closeMissionAlert = true
    }

    fun expandedAlert() {
        _expandedAlert.value = true
    }

    fun collapseAlert() {
        _expandedAlert.value = false
    }

    fun expandedAlertMissionPet() {
        _expandedMissionPetAlert.value = true
    }

    fun collapseAlertMissionPet() {
        _expandedMissionPetAlert.value = false
    }

    fun closeAlertMissionPet() {
        val eventValue = 10f
        val eventAttributes = mutableMapOf<String, String>()
        val semanticAttributes = SemanticAttributes()
        Airbridge.trackEvent(
            "airbridge.missionpet.set",
            "mainmissionpet_popup_next",
            "mainmissionpet_popup_next_label",
            eventValue,
            eventAttributes,
            semanticAttributes.toMap()
        )

        _closeMissionPetAlert.value = true
        _expandedMissionPetAlert.value = false
        AppConstants.closeMissionPetAlert = true
    }

    fun goToMissionPet() {
        _moveToMissionPet.emit()
    }

    fun goToWalk() {
        _moveToWalk.emit()
    }

    fun goToMission() {
        _moveToMission.emit()
    }

    fun goToPetWalkHistory(petId: Int, viewMode: String) {

    }

    private fun processDashboardData(
        context: Context,
        dashboardData: DashboardData,
        lat: Double,
        lng: Double,
        needUpdate: Boolean
    ) {
        speechBubbleUrl = dashboardData.speechBubble?.linkURL ?: ""
        petInfoList = listOf(dashboardData.pet)

        _petMessage.value = dashboardData.petMessage

        _walkDayCount.value = dashboardData.walk.dayCount.toInt()
        _walkTotalTime.value = dashboardData.walk.totalTime

        _walkTotalDistance.apply {
            if (dashboardData.walk.totalDistance == "0.0") {
                value = "0.00"
            } else {
                value = dashboardData.walk.totalDistance
            }
        }

        try {
            if (dashboardData.walk.statsType == "week") _walkStatsType.value = "이번 주"
            else if (dashboardData.walk.statsType == "lastWeek") _walkStatsType.value = "지난주"
            else _walkStatsType.value = ""

            val weather = dashboardData.weather.value ?: "흐림"
            _walkIndex.value = weather
            _walkIndexDay.value = "날씨 · "
        } catch (e :Exception) {
            //
        }

        _isWalkablePet.value = dashboardData.pet.isPossibleWalk
        _walkablePetSetting.value = !dashboardData.pet.isPossibleWalk
        _invitationWalk.value = dashboardData.pet.isPossibleWalk && dashboardData.walk.dayCount.toInt() == 0

        petId = dashboardData.pet.id
        _petName.value = dashboardData.pet.name
        _petImage.value = dashboardData.pet.profileImageURL

        val skyState = dashboardData.weather.skyState ?: 3
        _updateAnimation.emit(DashboardAnimationInfo(skyState, dashboardData))

        if (AppConstants.closeMissionAlert.not()) {
            _missionAlert.value = dashboardData.rewardMissionAlert
        }

        if (AppConstants.closeMissionPetAlert.not()) {
            _missionPetAlert.value = dashboardData.missionPetSettingAlert
        }

        processPopup(dashboardData.popup)
        updateCityName(context, lat, lng)
        if(needUpdate) getPingZoneFriend(dashboardData.pet.id)
    }

    /**
     * city name 업데이트
     */
    private fun updateCityName(context: Context, lat: Double, lng: Double) {
        val geocoder = Geocoder(context, Locale.KOREA)
        var geoCity: List<Address>? = null

        try {
            geoCity = geocoder.getFromLocation(lat, lng, 1)
            if (geoCity.size == 0) return

            if(geoCity?.get(0)?.thoroughfare != null) {
                _cityName.value = "위치 · ${geoCity.get(0).thoroughfare}"
            } else if(geoCity?.get(0)?.subLocality != null){
                _cityName.value = "위치 · ${geoCity.get(0).subLocality}"
            } else if(geoCity?.get(0)?.locality != null) {
                _cityName.value = "위치 · ${geoCity.get(0).locality}"
            } else {
                _cityName.value = "위치 · 확인 불가"
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    private fun processPopup(popupList: List<Popup>) {
        val doNotShowList = preferences.getDoNotShowPopupIdList()
        val lastDate = preferences.getLastDate()
        val currentDate = LocalDate.now().toString()

        if (lastDate != currentDate) {
            preferences.removeAllClosePopup()
            preferences.setLastDate(currentDate)
        }

        val targetList = popupList.toMutableList()
        for (popup in popupList) {
            for (id in doNotShowList) {
                if (popup.id == id.toInt()) {
                    targetList.remove(popup)
                    break
                }
            }
        }

        if(targetList.isNotEmpty()) showHomePopup.emit(popupList)
    }

    /**
     * 핑존에서 만난 친구
     *
     * @param petId
     */
    private fun getPingZoneFriend(petId: Int) = Coroutines.main(this) {
        val response = dashboardRepo.getPingZoneFriends(AppConstants.AUTH_KEY, petId)
        when (response) {
            is Resource.Success -> {
                LogUtil.log("ping zone friend size : ${response.value.data.pingzoneMeetPets.size}")
                _friendInfoList.value = response.value.data.pingzoneMeetPets
                if (response.value.data.pingzoneMeetPets.size == 1) {
                    val friendInfo = response.value.data.pingzoneMeetPets[0]
                    _friendName.value = friendInfo.name
                    _friendDescription.value = "${friendInfo.meetDatetime} . ${friendInfo.gender} . ${friendInfo.breed}"
                    _friendProfile.value = friendInfo.profileImageURL
                }
            }
        }
    }
}