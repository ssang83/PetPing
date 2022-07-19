package ai.comake.petping.ui.profile.make.family

import ai.comake.petping.AppConstants
import ai.comake.petping.Event
import ai.comake.petping.api.Resource
import ai.comake.petping.data.repository.UserDataRepository
import ai.comake.petping.data.vo.ErrorResponse
import ai.comake.petping.data.vo.FamilyProfile
import ai.comake.petping.emit
import ai.comake.petping.util.Coroutines
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * android-petping-2
 * Class: FamilyViewModel
 * Created by cliff on 2022/06/08.
 *
 * Description:
 */
@HiltViewModel
class FamilyViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var userRepo:UserDataRepository

    val familyCode = MutableLiveData<String>().apply { value = "" }

    val showErrorPopup = MutableLiveData<Event<ErrorResponse>>()
    val moveToFamilyCodeGuide = MutableLiveData<Event<Unit>>()
    val moveToFamilyConfirm = MutableLiveData<Event<FamilyProfile>>()

    /**
     * 가족 코드 서버에서 체크
     */
    fun confirm() = Coroutines.main(this) {
        val response =
            userRepo.getFamilyProfile(AppConstants.AUTH_KEY, AppConstants.ID, familyCode.value!!)

        when (response) {
            is Resource.Success -> {
                moveToFamilyConfirm.emit(response.value.data)
            }

            is Resource.Error -> {
                response.errorBody?.let { showErrorPopup.emit(it) }
            }
        }
    }

    fun moveToGuide() {
        moveToFamilyCodeGuide.emit()
    }
}