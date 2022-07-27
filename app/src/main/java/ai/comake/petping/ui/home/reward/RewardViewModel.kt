package ai.comake.petping.ui.home.reward

import ai.comake.petping.AppConstants
import ai.comake.petping.Event
import ai.comake.petping.api.Resource
import ai.comake.petping.data.repository.ShopRepository
import ai.comake.petping.data.vo.*
import ai.comake.petping.emit
import ai.comake.petping.util.toPingFormat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * android-petping-2
 * Class: RewardViewModel
 * Created by cliff on 2022/02/09.
 *
 * Description:
 */
@HiltViewModel
class RewardViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var shopRepository: ShopRepository

    private val _moveToOngoingDetail = MutableLiveData<Event<OngoingMission>>()
    val moveToOngoingDetail:LiveData<Event<OngoingMission>>
        get() = _moveToOngoingDetail

    private val _moveToCompletionDetail = MutableLiveData<Event<CompletionMission>>()
    val moveToCompletionDetail: LiveData<Event<CompletionMission>>
        get() = _moveToCompletionDetail

    private val _moveToOngoingEventDetail = MutableLiveData<Event<OngoingMission>>()
    val moveToOngoingEventDetail: LiveData<Event<OngoingMission>>
        get() = _moveToOngoingEventDetail

    private val _moveToCompletionEventDetail = MutableLiveData<Event<CompletionMission>>()
    val moveToCompletionEventDetail:LiveData<Event<CompletionMission>>
        get() = _moveToCompletionEventDetail

    private val _moveToHistory = MutableLiveData<Event<Unit>>()
    val moveToHistory:LiveData<Event<Unit>>
        get() = _moveToHistory

    private val _moveToMissionPet = MutableLiveData<Event<Unit>>()
    val moveToMissionPet:LiveData<Event<Unit>>
        get() = _moveToMissionPet

    private val _updateMission = MutableLiveData<Event<Unit>>()
    val updateMission:LiveData<Event<Unit>>
        get() = _updateMission

    private val _earningPoint = MutableLiveData<String>().apply { value = "0" }
    val earningPoint:LiveData<String>
        get() = _earningPoint

    val ongoingMissionList = MutableLiveData<List<OngoingMission>>()
    val completionMissionList = MutableLiveData<List<CompletionMission>>()

    var ongoingMissionData: OngoingMissionData? = null
    var completionMissionData: CompletionMissionData? = null

    val ongoingMissions = ArrayList<OngoingMission>()
    val completionMissions = ArrayList<CompletionMission>()

    var availablePings: AvailablePings? = null
    var updateOngoing = false
    var updateCompletion = false

    fun loadData() {
        ongoingMissions.clear()
        getAvailablePoint()
        getOngoingMission()

        if (completionMissionData == null) {
            completionMissions.clear()
            getCompletionMission()
        }
    }

    /**
     * 진행 중인 미션 상세 화면으로 이동
     */
    fun goToDetail(ongoingMission: OngoingMission) {
        _moveToOngoingDetail.emit(ongoingMission)
    }

    /**
     * 완료 미션 상세 화면으로 이동
     */
    fun goToDetail(completeMission: CompletionMission) {
        _moveToCompletionDetail.emit(completeMission)
    }

    fun goToEventDetail(ongoingMission: OngoingMission) {
        _moveToOngoingEventDetail.emit(ongoingMission)
    }

    fun goToEventDetail(completeMission: CompletionMission) {
        _moveToCompletionEventDetail.emit(completeMission)
    }

    fun goToHistory() {
        _moveToHistory.emit()
    }

    /**
     * 진행 중인 미션 더보기 클릭 시
     */
    fun getNextOngoingMission() {
        ongoingMissionData?.let { getOngoingMission(it.pageNo.toInt().inc()) }
    }

    fun goToMissionPet() {
        _moveToMissionPet.emit()
    }

    /**
     * 완료 미션 더보기 클릭 시
     */
    fun getNextCompletionMission() {
        completionMissionData?.let { getCompletionMission(it.pageNo.toInt().inc()) }
    }

    /**
     * 사용 가능 핑 데이터 받아옴
     */
    private fun getAvailablePoint() = viewModelScope.launch {
        val response = shopRepository.getAvailablePing(AppConstants.AUTH_KEY, AppConstants.ID)
        when (response) {
            is Resource.Success -> {
                _earningPoint.value = response.value.data.availablePings.toPingFormat()
                availablePings = response.value.data
            }
        }
    }

    /**
     * 진행중인 미션 데이터 받아옴
     */
    private fun getOngoingMission(page:Int = 1) = viewModelScope.launch {
        val response = shopRepository.getOngoingMission(
            AppConstants.AUTH_KEY,
            AppConstants.ID,
            page
        )
        when (response) {
            is Resource.Success -> {
                ongoingMissionData = response.value.data
                ongoingMissionList.value = response.value.data.ongoingMissions
                ongoingMissions.addAll(response.value.data.ongoingMissions)
                _updateMission.emit()
                updateOngoing = true
            }
        }
    }

    /**
     * 완료 미션 데이터 받아옴
     */
    private fun getCompletionMission(page: Int = 1) = viewModelScope.launch {
        val response = shopRepository.getCompleteMission(
            AppConstants.AUTH_KEY,
            AppConstants.ID,
            page
        )
        when (response) {
            is Resource.Success -> {
                completionMissionData = response.value.data
                completionMissionList.value = response.value.data.completionMissions
                completionMissions.addAll(response.value.data.completionMissions)
                _updateMission.emit()
                updateCompletion = true
            }
        }
    }
}