package ai.comake.petping.ui.history.reward

import ai.comake.petping.AppConstants
import ai.comake.petping.Event
import ai.comake.petping.UiState
import ai.comake.petping.api.Resource
import ai.comake.petping.data.repository.ShopRepository
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
 * Class: RewardHistoryViewModel
 * Created by cliff on 2022/02/10.
 *
 * Description:
 */
@HiltViewModel
class RewardHistoryViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var shopRepository: ShopRepository

    private val _totalPing = MutableLiveData<String>().apply { value = "0" }
    val totalPing:LiveData<String>
        get() = _totalPing

    private val _totalSaving = MutableLiveData<String>().apply { value = "0P" }
    val totalSaving:LiveData<String>
        get() = _totalSaving

    private val _totalUsing = MutableLiveData<String>().apply { value = "0P" }
    val totalUsing:LiveData<String>
        get() = _totalUsing

    private val _expiredSoon = MutableLiveData<String>().apply { value = "0P" }
    val expiredSoon:LiveData<String>
        get() = _expiredSoon

    private val _expiredPing = MutableLiveData<String>().apply { value = "0P" }
    val expiredPing:LiveData<String>
        get() = _expiredPing

    private val _uiState = MutableLiveData<Event<UiState>>()
    val uiState: LiveData<Event<UiState>>
        get() = _uiState

    fun loadData() = viewModelScope.launch {
        _uiState.emit(UiState.Loading)
        val response = shopRepository.getDetailPing(AppConstants.AUTH_KEY, AppConstants.ID)
        when (response) {
            is Resource.Success -> {
                _uiState.emit(UiState.Success)
                _totalSaving.value = "${response.value.data.totalSave.toPingFormat()}P"
                _totalUsing.value = "${response.value.data.totalUse.toPingFormat()}P"
                _expiredSoon.value = "${response.value.data.expireSoon.toPingFormat()}P"
                _expiredPing.value = "${response.value.data.expire.toPingFormat()}P"
            }
            else -> _uiState.emit(UiState.Failure(null))
        }

        val response1 = shopRepository.getAvailablePing(AppConstants.AUTH_KEY, AppConstants.ID)
        when (response1) {
            is Resource.Success -> {
                _uiState.emit(UiState.Success)
                _totalPing.value = response1.value.data.availablePings.toPingFormat()
            }
            else -> _uiState.emit(UiState.Failure(null))
        }
    }
}