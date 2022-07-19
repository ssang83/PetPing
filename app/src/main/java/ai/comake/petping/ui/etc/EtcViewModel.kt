package ai.comake.petping.ui.etc

import ai.comake.petping.AppConstants
import ai.comake.petping.Event
import ai.comake.petping.api.Resource
import ai.comake.petping.data.repository.UserDataRepository
import ai.comake.petping.data.vo.MyPageData
import ai.comake.petping.data.vo.MyPet
import ai.comake.petping.data.vo.PetProfileConfig
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
 * Class: EtcViewModel
 * Created by cliff on 2022/02/18.
 *
 * Description:
 */
@HiltViewModel
class EtcViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var userDataRepository: UserDataRepository

    private val _name = MutableLiveData<String>().apply { value = "스몰일" }
    val name:LiveData<String> get() = _name

    private val _email = MutableLiveData<String>().apply { value = "small01@smallticket.com" }
    val email:LiveData<String> get() = _email

    private val _isEmailAuth = MutableLiveData<Boolean>()
    val isEmailAuth:LiveData<Boolean> get() = _isEmailAuth

    private val _myPetItems = MutableLiveData<List<MyPet>>()
    val myPetItems:LiveData<List<MyPet>> get() = _myPetItems

    val isVisibleScrollView = MutableLiveData<Boolean>().apply { value = true }
    val moveToMemberInfo = MutableLiveData<Event<Unit>>()
    val moveToNotice = MutableLiveData<Event<Unit>>()
    val moveToInquiry = MutableLiveData<Event<Unit>>()
    val moveToAppInfo = MutableLiveData<Event<Unit>>()
    val moveToMissionPet = MutableLiveData<Event<Unit>>()
    val moveToWelcomeKit = MutableLiveData<Event<Unit>>()
    val moveToMakeProfile = MutableLiveData<Event<Unit>>()
    val moveToPetProfile = MutableLiveData<Event<PetProfileConfig>>()
    val uiSetUp = MutableLiveData<Event<Unit>>()

    var etcFragmentInfo: MyPageData? = null

    fun loadData() = viewModelScope.launch {
        val response = userDataRepository.getMyPageInfo(AppConstants.AUTH_KEY, AppConstants.ID)
        when(response) {
            is Resource.Success -> {
                val data = response.value.data
                etcFragmentInfo = data
                data.myInfos.name?.let { _name.value = it }
                data.myInfos.email?.let { _email.value = it }
                _isEmailAuth.value = data.myInfos.isEmailAuth
                _myPetItems.value = data.myPets
                uiSetUp.emit()
            }
        }
    }

    fun goToMemberInfo() {
        moveToMemberInfo.emit()
    }

    fun goToNotice() {
        moveToNotice.emit()
    }

    fun goToQuestion() {
        moveToInquiry.emit()
    }

    fun goToAppInfo() {
        moveToAppInfo.emit()
    }

    fun goToRepresentative() {
        moveToMissionPet.emit()
    }

    fun goToWeclomKit() {
        moveToWelcomeKit.emit()
    }

    fun goToMakeProfile() {
        moveToMakeProfile.emit()
    }

    fun goToPetProfile(petId:Int, viewMode:String) {
        val config = PetProfileConfig(
            petId, viewMode
        )
        moveToPetProfile.emit(config)
    }
}