package ai.comake.petping.ui.profile.edit

import ai.comake.petping.*
import ai.comake.petping.api.Resource
import ai.comake.petping.data.repository.PetRepository
import ai.comake.petping.data.vo.ErrorResponse
import ai.comake.petping.util.*
import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.regex.Pattern
import javax.inject.Inject

/**
 * android-petping-2
 * Class: ProfileEditViewModel
 * Created by cliff on 2022/03/29.
 *
 * Description:
 */
@HiltViewModel
class ProfileEditViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var repo: PetRepository

    val petName = MutableLiveData<String>().apply { value = "" }
    val breed = MutableLiveData<String>().apply { value = "" }
    val birth = MutableLiveData<String>().apply { value = "" }
    val weight = MutableLiveData<String>().apply { value = "" }
    val petRns = MutableLiveData<String>().apply { value = "" }
    val petProfileImage = MutableLiveData<String>().apply { value = "" }
    val reason = MutableLiveData<String>().apply { value = "" }
    val petWalkableNot = MutableLiveData<Boolean>().apply { value = false }
    val birthValid = MutableLiveData<Boolean>().apply { value = false }
    val isPublic = MutableLiveData<Boolean>().apply { value = false }
    val gender = MutableLiveData<Int>().apply { value = -1 }
    val profileId = MutableLiveData<Int>().apply { value = -1 }
    val profileType = MutableLiveData<Int>().apply { value = -1 }

    val deleteErrorPopup = MutableLiveData<Event<ErrorResponse>>()
    val deletePopup = MutableLiveData<Event<Unit>>()
    val breedUpdated = MutableLiveData<Event<Unit>>()
    val uiState = MutableLiveData<Event<UiState>>()
    val disableInputByInsurance = MutableLiveData<Event<Int>>()
    val moveToHome = MutableLiveData<Event<Unit>>()
    val moveToBack = MutableLiveData<Event<Unit>>()
    val pickPicture = MutableLiveData<Event<Unit>>()
    val birthErrorUI = MutableLiveData<Event<Unit>>()

    var needPhotoUpdated = false
    var breedList:List<String> = listOf()
    var petId = -1

    fun loadData(_petId: Int) {
        petId = _petId
        uiState.emit(UiState.Loading)
        viewModelScope.launch {
            val response = repo.getPetProfile(AppConstants.AUTH_KEY, _petId, AppConstants.ID)
            when (response) {
                is Resource.Success -> {
                    uiState.emit(UiState.Success)
                    petName.value = response.value.data.name
                    breed.value = response.value.data.breed
                    weight.value = response.value.data.weight.toString()
                    petRns.value = response.value.data.petRn ?: ""
                    petProfileImage.value = response.value.data.profileImageURL
                    petWalkableNot.value = response.value.data.isPossibleWalk.not()
                    reason.value = response.value.data.noWalkReason ?: ""
                    profileId.value = response.value.data.profileId
                    isPublic.value = response.value.data.isPublic

                    val tempBirth = response.value.data.birth.split("-")
                    birth.value = tempBirth[0].substring(2, 4) + tempBirth[1] + tempBirth[2]

                    if (response.value.data.profileType == 1) {
                        profileType.value = response.value.data.profileType
                        disableInputByInsurance.emit(response.value.data.gender)
                    } else {
                        gender.value = response.value.data.gender
                    }
                }
            }
        }
    }

    fun updateProfile(context: Context) {
        uiState.emit(UiState.Loading)
        viewModelScope.launch {
            val photoUrl = if (needPhotoUpdated) petProfileImage.value.toString() else null
            val body = makePetProfileModifyBody(
                petName.value.toString(),
                gender.value.toString(),
                birth.value.toString(),
                breed.value?.trim().toString(),
                weight.value.toString(),
                petWalkableNot.value?.not().toString(),
                if(petWalkableNot.value!!) reason.value.toString() else "",
                "",
                isPublic.value.toString()
            )

            if (photoUrl != null) {
                val contents = context.contentResolver.openInputStream(Uri.parse(photoUrl))
                val outputStream = getBitmapFromInputStream(contents!!, context, photoUrl)
                val filePart = MultipartBody.Part.createFormData(
                    "photoFile", "photoFile.png", outputStream.toByteArray().toRequestBody(
                        "image/png".toMediaTypeOrNull(), 0,
                        outputStream.size()
                    )
                )

                val response = repo.modifyPetProfileWithFile(AppConstants.AUTH_KEY, profileId.value!!, body, filePart)
                when (response) {
                    is Resource.Success -> {
                        uiState.emit(UiState.Success)
                        moveToBack.emit()
                    }
                    else -> uiState.emit(UiState.Failure(null))
                }
            } else {
                val response = repo.modifyPetProfile(AppConstants.AUTH_KEY, profileId.value!!, body)
                when (response) {
                    is Resource.Success -> {
                        uiState.emit(UiState.Success)
                        moveToBack.emit()
                    }
                    else -> uiState.emit(UiState.Failure(null))
                }
            }
        }
    }

    fun getBreedList() = viewModelScope.launch {
        uiState.emit(UiState.Loading)
        val response = repo.getBreedList(AppConstants.AUTH_KEY)
        when (response) {
            is Resource.Success -> {
                uiState.emit(UiState.Success)
                breedList = response.value.data.breeds
                breedUpdated.emit()
            }
            else -> uiState.emit(UiState.Failure(null))
        }
    }

    fun delete() = viewModelScope.launch {
        uiState.emit(UiState.Loading)
        val response = repo.deletePetProfile(AppConstants.AUTH_KEY, profileId.value!!)
        when (response) {
            is Resource.Success -> {
                uiState.emit(UiState.Success)
                moveToHome.emit()
            }
            is Resource.Failure -> {
                uiState.emit(UiState.Failure(null))
                response.errorBody?.let { errorBody ->
                    val errorResponse = getErrorBodyConverter().convert(errorBody)!!
                    deleteErrorPopup.emit(errorResponse)
                }
            }
        }
    }

    fun deleteProfile() {
        deletePopup.emit()
    }

    /**
     * name TextWatcher
     *
     * @param name
     */
    fun onNameTextChanged(name: CharSequence) {
        petName.value = name.toString().trim()
    }

    /**
     * breed TextWatcher
     *
     * @param _breed
     */
    fun onBreedTextChanged(text: CharSequence) {
        breed.value = text.toString().trim()
    }

    /**
     * birth TextWatcher
     *
     * @param _birth
     */
    fun onBirthTextChanged(text: CharSequence) {
        birth.value = text.toString().trim()
        if (birth.value.toString().length == 6) {
            if (text.toString().contains(".")) {
                birthErrorUI.emit()
                birthValid.value = false
                return
            }

            val current = LocalDate.now()
            val formatter = DateTimeFormatter.ofPattern("yyMMdd")
            val formatted = current.format(formatter)
            if (validationDate(birth.value.toString()).not()
                || Integer.parseInt(formatted) < Integer.parseInt(birth.value.toString())) {
                birthErrorUI.emit()
                birthValid.value = false
            } else {
                birthValid.value = true
            }
        }
    }

    /**
     * weight TextWatcher
     *
     * @param _weight
     */
    fun onWeightTextChanged(text: CharSequence) {
        weight.value = text.toString().trim()
    }

    fun pickPicture() {
        pickPicture.emit()
    }

    /**
     * 수정 완료 버튼 활성화 조건
     */
    fun isValidInfo(
        petName: String,
        bread: String,
        birth: String,
        weight: String
    ): Boolean {
        return (petName.isNotEmpty() && Pattern.compile(HANGUEL_PATTERN_NEW).matcher(petName).matches())
                && (bread.isNotEmpty() && bread.trim().isNotEmpty() && Pattern.compile(HANGUEL_PATTERN_ADD_SPACE).matcher(bread).matches())
                && birth != "" && birthValid.value!! && birth.length == 6
                && weight != "." && Pattern.compile(NUMBER_DOT_PATTERN).matcher(weight).matches()
    }

    /**
     * 유효한 날짜인지 체크하는 함수
     */
    private fun validationDate(checkDate: String): Boolean {
        return try {
            val dateFormat = SimpleDateFormat("yyMMdd", Locale.KOREAN)
            dateFormat.isLenient = false
            dateFormat.parse(checkDate)
            true
        } catch (e: ParseException) {
            false
        }
    }
}