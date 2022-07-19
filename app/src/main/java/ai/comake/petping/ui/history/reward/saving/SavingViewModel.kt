package ai.comake.petping.ui.history.reward.saving

import ai.comake.petping.AppConstants
import ai.comake.petping.Event
import ai.comake.petping.UiState
import ai.comake.petping.api.Resource
import ai.comake.petping.data.repository.ShopRepository
import ai.comake.petping.data.vo.ExpiredHistory
import ai.comake.petping.data.vo.ExpiredPings
import ai.comake.petping.data.vo.SavingHistory
import ai.comake.petping.data.vo.SavingPings
import ai.comake.petping.emit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * android-petping-2
 * Class: SavingViewModel
 * Created by cliff on 2022/02/10.
 *
 * Description:
 */
@HiltViewModel
class SavingViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var shopRepository: ShopRepository

    private val _updatePoint = MutableLiveData<Event<Unit>>()
    val updatePoint: LiveData<Event<Unit>> = _updatePoint

    val savingHistoryData = MutableLiveData<List<SavingHistory>>()
    val expireHistoryData = MutableLiveData<List<ExpiredHistory>>()

    val savingHistories = ArrayList<SavingHistory>()
    val expiredHistories = ArrayList<ExpiredHistory>()

    var savingPointData: SavingPings? = null
    var expiredPointData: ExpiredPings? = null

    /**
     * 사용기간 만료 리스트 아래의 더 보기 버튼 클릭 시
     *
     */
    fun getNextExpirationPoints() {
        expiredPointData?.let {
            getExpirationPoints(it.pageNo.toInt().inc())
        }
    }

    /**
     * 사용기간이 만료되지 않은 적립 핑 아래의 더 보기 버튼 클릭 시
     */
    fun getNextSavingPoints() {
        savingPointData?.let {
            getSavingPoints(it.pageNo.toInt().inc())
        }
    }

    fun getExpirationPoints(page: Int = 1) = viewModelScope.launch {
        val response = shopRepository.getExpirePing(AppConstants.AUTH_KEY, AppConstants.ID, page)
        when (response) {
            is Resource.Success -> {
                expiredPointData = response.value.data
                expiredHistories.addAll(response.value.data.expirationPings)
                expireHistoryData.value = response.value.data.expirationPings
                _updatePoint.emit()
            }
        }
    }

    fun getSavingPoints(page: Int = 1) = viewModelScope.launch {
        val response = shopRepository.getSavingPing(AppConstants.AUTH_KEY, AppConstants.ID, page)
        when (response) {
            is Resource.Success -> {
                savingPointData = response.value.data
                savingHistories.addAll(response.value.data.historyPings)
                savingHistoryData.value = response.value.data.historyPings
                _updatePoint.emit()
            }
        }
    }

}