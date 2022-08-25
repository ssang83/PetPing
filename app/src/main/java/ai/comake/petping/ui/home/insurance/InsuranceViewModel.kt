package ai.comake.petping.ui.home.insurance

import ai.comake.petping.AppConstants
import ai.comake.petping.Event
import ai.comake.petping.api.Resource
import ai.comake.petping.data.repository.InsuranceRepository
import ai.comake.petping.data.repository.PetRepository
import ai.comake.petping.data.vo.ErrorResponse
import ai.comake.petping.emit
import ai.comake.petping.util.Coroutines
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
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

    @Inject
    lateinit var repo:InsuranceRepository

    @Inject
    lateinit var petRepository: PetRepository

    private val _moveToInsuranceApply = MutableLiveData<Event<String>>()
    val moveToInsuranceApply:LiveData<Event<String>>
        get() = _moveToInsuranceApply

    private val _moveToHanhwa = MutableLiveData<Event<String>>()
    val moveToHanhwa:LiveData<Event<String>>
        get() = _moveToHanhwa

    private val _moveToDB = MutableLiveData<Event<String>>()
    val moveToDB:LiveData<Event<String>>
        get() = _moveToDB

    private val _showErrorPopup = MutableLiveData<Event<ErrorResponse>>()
    val showErrorPopup:LiveData<Event<ErrorResponse>> get() = _showErrorPopup

    private val _showProfilePopup = MutableLiveData<Event<Unit>>()
    val showProfilePopup:LiveData<Event<Unit>> get() = _showProfilePopup

    val ballonStatus = MutableLiveData<Boolean>().apply { value = false }

    var hanhwaUrl = ""
    var dbUrl = ""
    var claimUrl = ""

    val scrollChangeListener = object : View.OnScrollChangeListener {
        override fun onScrollChange(
            v: View?,
            scrollX: Int,
            scrollY: Int,
            oldScrollX: Int,
            oldScrollY: Int
        ) {
            ballonStatus.value = false
        }
    }

    fun loadData() = Coroutines.main(this) {
        val response = repo.getJoinInsurance(AppConstants.AUTH_KEY)
        when (response) {
            is Resource.Success -> {
                if (response.value.status == "200") {
                    hanhwaUrl = response.value.data.hanwhaJoinPage
                    dbUrl = response.value.data.dbJoinPage
                    claimUrl = response.value.data.claimPage
                } else {
                    _showErrorPopup.emit(response.value.error)
                }
            }
        }
    }

    fun getPetList(type:String) = Coroutines.main(this) {
        val response = petRepository.getPetList(AppConstants.AUTH_KEY, AppConstants.ID)
        when (response) {
            is Resource.Success -> {
                if (response.value.data.pets.size > 0 && response.value.data.pets[0].isFamilyProfile.not()) {
                    if (type == "hanhwa") {
                        _moveToHanhwa.emit(hanhwaUrl)
                    } else if(type == "db") {
                        _moveToDB.emit(dbUrl)
                    } else {
                        _moveToInsuranceApply.emit(claimUrl)
                    }
                } else {
                    _showProfilePopup.emit()
                }
            }
            is Resource.Error -> {
                response.errorBody?.let { error ->
                    if (error.code == "C2070") {
                        _showProfilePopup.emit()
                    }
                }
            }
        }
    }

    fun showBallon() {
        ballonStatus.value = true
    }

    fun closeBallon() {
        ballonStatus.value = false
    }

    fun moveHanhwaJoin() {
        getPetList("hanhwa")
    }

    fun moveDBJoin() {
        getPetList("db")
    }

    fun moveToApply() {
        getPetList("")
    }
}