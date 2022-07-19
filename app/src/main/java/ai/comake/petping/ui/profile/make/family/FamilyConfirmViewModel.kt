package ai.comake.petping.ui.profile.make.family

import ai.comake.petping.*
import ai.comake.petping.api.Resource
import ai.comake.petping.data.repository.UserDataRepository
import ai.comake.petping.data.vo.FamilyProfile
import ai.comake.petping.util.Coroutines
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import co.ab180.airbridge.Airbridge
import co.ab180.airbridge.event.model.SemanticAttributes
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * android-petping-2
 * Class: FamilyConfirmViewModel
 * Created by cliff on 2022/06/08.
 *
 * Description:
 */
@HiltViewModel
class FamilyConfirmViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var userRepo : UserDataRepository

    private val _profileUrl = MutableLiveData<String>().apply { value = "" }
    val profileUrl:LiveData<String> get() = _profileUrl

    private val _name = MutableLiveData<String>().apply { value = "" }
    val name:LiveData<String> get() = _name

    private val _breed = MutableLiveData<String>().apply { value = "" }
    val breed:LiveData<String> get() = _breed

    private val _age = MutableLiveData<String>().apply { value = "" }
    val age:LiveData<String> get() = _age

    val showSuccessPopup = MutableLiveData<Event<Unit>>()
    val showErrorPopup = MutableLiveData<Event<String>>()
    val uiState = MutableLiveData<Event<UiState>>()

    var familyCode = ""
    var petId = -1

    fun init(_familyCode:String, profile:FamilyProfile) {
        familyCode = _familyCode
        petId = profile.petId

        _profileUrl.value = profile.profileImageURL
        _name.value = "#${profile.name}"
        _breed.value = "#${profile.breed}"
        _age.value = "#${profile.age}"
    }

    fun registerFamilyPet() {
        val eventValue = 10f
        val eventAttributes = mutableMapOf<String, String>()
        val semanticAttributes = SemanticAttributes()
        Airbridge.trackEvent(
            "airbridge.petprofile.make",
            "familycode_button_familyinsign",
            "familycode_button_familyinsign_label",
            eventValue,
            eventAttributes,
            semanticAttributes.toMap()
        )

        uiState.emit(UiState.Loading)
        Coroutines.main(this) {
            val body = makeRegisterFamilyProfileBody(petId, familyCode)
            val response = userRepo.registerFamilyProfile(AppConstants.AUTH_KEY, body)
            when (response) {
                is Resource.Success -> {
                    uiState.emit(UiState.Success)
                    showSuccessPopup.emit()
                }
                is Resource.Error -> {
                    uiState.emit(UiState.Failure(null))
                    response.errorBody?.let { showErrorPopup.emit(it.message) }
                }
                else -> uiState.emit(UiState.Failure(null))
            }
        }
    }
}