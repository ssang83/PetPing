package ai.comake.petping.ui.history.reward.using

import ai.comake.petping.AppConstants
import ai.comake.petping.Event
import ai.comake.petping.api.Resource
import ai.comake.petping.data.repository.ShopRepository
import ai.comake.petping.data.vo.UsingHistory
import ai.comake.petping.data.vo.UsingPings
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
 * Class: UsingViewModel
 * Created by cliff on 2022/02/10.
 *
 * Description:
 */
@HiltViewModel
class UsingViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var shopRepository: ShopRepository

    private val _updatePoint = MutableLiveData<Event<Unit>>()
    val updatePoint: LiveData<Event<Unit>> = _updatePoint

    val usingHistoryData = MutableLiveData<List<UsingHistory>>()

    var usingPointData: UsingPings? = null
    val usingHistories = ArrayList<UsingHistory>()

    fun getNextUsingPoints() {
        usingPointData?.let {
            getUsingPoints(it.pageNo.toInt().inc())
        }
    }

    fun getUsingPoints(page:Int = 1) = viewModelScope.launch {
        val response = shopRepository.getUsingPing(AppConstants.AUTH_KEY, AppConstants.ID, page)
        when (response) {
            is Resource.Success -> {
                usingPointData = response.value.data
                usingHistories.addAll(response.value.data.historyPings)
                usingHistoryData.value = response.value.data.historyPings
                _updatePoint.emit()
            }
        }
    }
}