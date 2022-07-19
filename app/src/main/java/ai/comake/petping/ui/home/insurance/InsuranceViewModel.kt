package ai.comake.petping.ui.home.insurance

import ai.comake.petping.AppConstants
import ai.comake.petping.Event
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
 * Class: InsuranceViewModel
 * Created by cliff on 2022/02/09.
 *
 * Description:
 */
@HiltViewModel
class InsuranceViewModel @Inject constructor() : ViewModel() {

    private val _moveToInsurance = MutableLiveData<Event<Unit>>()
    val moveToInsurance:LiveData<Event<Unit>>
        get() = _moveToInsurance

    val loginIsVisible = MutableLiveData<Boolean>().apply { value = AppConstants.LOGIN_HEADER_IS_VISIBLE }
    val profileIsVisible = MutableLiveData<Boolean>().apply { value = AppConstants.PROFILE_HEADER_IS_VISIBLE }

    fun loadData() = viewModelScope.launch {

    }

    fun goToInsurance() {
        _moveToInsurance.emit()
    }
}