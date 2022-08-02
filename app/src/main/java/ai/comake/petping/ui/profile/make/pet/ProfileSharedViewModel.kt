package ai.comake.petping.ui.profile.make.pet

import ai.comake.petping.*
import ai.comake.petping.api.Resource
import ai.comake.petping.data.repository.PetRepository
import ai.comake.petping.util.*
import android.content.Context
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import co.ab180.airbridge.Airbridge
import co.ab180.airbridge.event.model.SemanticAttributes
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.http.Multipart
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.regex.Pattern
import javax.inject.Inject

/**
 * android-petping-2
 * Class: SharedViewModel
 * Created by cliff on 2022/06/08.
 *
 * Description:
 */
@HiltViewModel
class ProfileSharedViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var petRepo : PetRepository

    val petName = MutableLiveData<String>().apply { value = "" }
    val gender = MutableLiveData<Int>().apply { value = -1 }
    val petGender = MutableLiveData<String>().apply { value = "" }
    val petAge = MutableLiveData<String>().apply { value = "" }
    val breed = MutableLiveData<String>().apply { value = "" }
    val birth = MutableLiveData<String>().apply { value = "" }
    val weight = MutableLiveData<String>().apply { value = "" }
    val imageSrc = MutableLiveData<String>().apply { value = "" }

    // Event
    val uiState = MutableLiveData<Event<UiState>>()
    val moveToSecond = MutableLiveData<Event<Unit>>()
    val moveToLast = MutableLiveData<Event<Unit>>()
    val birthErrorUI = MutableLiveData<Event<String>>()
    val moveToFirst = MutableLiveData<Event<Unit>>()
    val moveToHome = MutableLiveData<Event<Unit>>()
    val moveToMissionPet = MutableLiveData<Event<Unit>>()
    val imagePick = MutableLiveData<Event<Unit>>()
    val breedUpdate = MutableLiveData<Event<Unit>>()
    val clickBreed = MutableLiveData<Event<String>>()

    var breedList : List<String> = listOf()

    /**
     * 띄어쓰기 불가
     */
    val petNameTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: Editable?) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            petName.value = petName.value.toString().replace(" ", "")
        }
    }

    fun getBreedList() = Coroutines.main(this) {
        uiState.emit(UiState.Loading)
        val response = petRepo.getBreedList(AppConstants.AUTH_KEY)
        when (response) {
            is Resource.Success -> {
                uiState.emit(UiState.Success)
                breedList = response.value.data.breeds
                breedUpdate.emit()
            }
            else -> uiState.emit(UiState.Failure(null))
        }
    }

    fun isValidInfo(petName: String, gender: Int): Boolean {
        return petName.isNotEmpty() && Pattern.compile(HANGUEL_PATTERN_NEW_FIX).matcher(petName).matches() && gender != -1
    }

    fun isValid(birth: String, breed: String): Boolean {
        return birth != "" && birth.length == 6 && birthValid() && breed != "" && breed.trim().isNotEmpty() && breedValid()
    }

    fun validation(weight:String): Boolean {
        return weight != "." && Pattern.compile(NUMBER_DOT_PATTERN).matcher(weight).matches()
    }

    fun goToSecond() {
        moveToSecond.emit()
    }

    fun goToLast() {
        moveToLast.emit()
    }

    fun goToNewProfileFirst() {
        moveToFirst.emit()
    }

    fun goToNewProfileSecond() {
        moveToSecond.emit()
    }

    fun pickPicture() {
        imagePick.emit()
    }

    fun selectBreed(breed: String) {
        clickBreed.emit(breed)
    }

    fun resetLiveData() {
        petName.value = ""
        gender.value = -1
        petGender.value = ""
        petAge.value = ""
        breed.value = ""
        birth.value = ""
        weight.value = ""
        imageSrc.value = ""
    }

    fun saveAndComplete(context: Context) = Coroutines.main(this) {
        val eventValue = 10f
        val eventAttributes = mutableMapOf<String, String>()
        val semanticAttributes = SemanticAttributes()
        Airbridge.trackEvent(
            "airbridge.petprofile.make",
            "petphoto_button_nextstep",
            "petphoto_button_nextstep_label",
            eventValue,
            eventAttributes,
            semanticAttributes.toMap()
        )

        uiState.emit(UiState.Loading)
        val body = makeNewProfileBody(
            AppConstants.ID,
            petName.value.toString(),
            gender.value.toString(),
            birth.value.toString(),
            breed.value.toString(),
            weight.value.toString(),
            "1",
            AppConstants.colorList[2],
            "0",
            AppConstants.colorList[6],
            "0",
            AppConstants.colorList[2],
            7,
            "",
            ""
        )

        var filePart:MultipartBody.Part? = null
        if (imageSrc.value.toString().isNotEmpty()) {
            val contents = context.contentResolver.openInputStream(Uri.parse(imageSrc.value.toString()))
            val outputStream = getBitmapFromInputStream(contents!!, context, imageSrc.value.toString())
            filePart = MultipartBody.Part.createFormData(
                "photoFile", "photoFile.png", outputStream.toByteArray().toRequestBody(
                    "image/png".toMediaTypeOrNull(), 0,
                    outputStream.size()
                )
            )
        }

        val response = petRepo.makeProfile(AppConstants.AUTH_KEY, body, filePart)
        when (response) {
            is Resource.Success -> {
                uiState.emit(UiState.Success)
                resetLiveData()
                if (response.value.data.isMissionPet.not()) {
                    moveToMissionPet.emit()
                } else {
                    moveToHome.emit()
                }
            }
            else -> uiState.emit(UiState.Failure(null))
        }
    }

    /**
     * 생년월일 체크 로직
     */
    private fun birthValid(): Boolean {
        if (birth.value.toString().length == 6) {
            if (birth.value.toString().contains(".")) {
                birthErrorUI.emit("정확한 생년월일을 입력해 주세요.")
                return false
            }

            val current = LocalDate.now()
            val formatter = DateTimeFormatter.ofPattern("yyMMdd")
            val formatted = current.format(formatter)
            if (validationDate(birth.value.toString()).not()
                || Integer.parseInt(formatted) < Integer.parseInt(birth.value.toString())) {
                birthErrorUI.emit("정확한 생년월일을 입력해 주세요.")
                return false
            } else {
                return true
            }
        }
        return false
    }
    /**
     * 견종 체크 로직
     */
    private fun breedValid(): Boolean {
        val breedPattern = Pattern.compile(HANGUEL_PATTERN_ADD_SPACE)
        if (breed.value.toString().length > 0) {
            if (!breedPattern.matcher(breed.value.toString()).matches()) {
                return false
            } else {
                return true
            }
        }
        return false
    }

    /**
     * 유효한 날짜인지 체크하는 함수
     */
    private fun validationDate(checkDate: String): Boolean {
        return try {
            val dateFormat = SimpleDateFormat("yyMMdd")
            dateFormat.isLenient = false
            dateFormat.parse(checkDate)
            true
        } catch (e: ParseException) {
            false
        }
    }
}