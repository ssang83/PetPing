package ai.comake.petping.ui.profile.make.pet

import ai.comake.petping.*
import ai.comake.petping.api.Resource
import ai.comake.petping.data.repository.PetRepository
import ai.comake.petping.util.*
import android.content.Context
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
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

    // 인터렉션 관련 사용하는 변수들...
    val petNameInputStatus = MutableLiveData<Boolean>().apply { value = false }
    val petNameValidation = MutableLiveData<Boolean>().apply { value = true }
    val petNameFocusHintVisible = MutableLiveData<Boolean>().apply { value = false }
    val petNameLineStatus = MutableLiveData<Boolean>().apply { value = false }
    val petNameHelperVisible = MutableLiveData<Boolean>().apply { value = false }
    val breedInputStatus = MutableLiveData<Boolean>().apply { value = false }
    val breedValidation = MutableLiveData<Boolean>().apply { value = true }
    val breedFocusHintVisible = MutableLiveData<Boolean>().apply { value = false }
    val breedLineStatus = MutableLiveData<Boolean>().apply { value = false }
    val birthInputStatus = MutableLiveData<Boolean>().apply { value = false }
    val birthValidation = MutableLiveData<Boolean>().apply { value = true }
    val birthFocusHintVisible = MutableLiveData<Boolean>().apply { value = false }
    val birthLineStatus = MutableLiveData<Boolean>().apply { value = false }
    val birthHelperVisible = MutableLiveData<Boolean>().apply { value = false }
    val weightInputStatus = MutableLiveData<Boolean>().apply { value = false }
    val weightValidation = MutableLiveData<Boolean>().apply { value = true }
    val weightFocusHintVisible = MutableLiveData<Boolean>().apply { value = false }
    val weightLineStatus = MutableLiveData<Boolean>().apply { value = false }
    val weightHelperVisible = MutableLiveData<Boolean>().apply { value = false }


    // Event
    val uiState = MutableLiveData<Event<UiState>>()
    val moveToSecond = MutableLiveData<Event<Unit>>()
    val moveToLast = MutableLiveData<Event<Unit>>()
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
            petNameInputStatus.apply {
                if (s?.length!! > 0) {
                    value = true
                } else {
                    value = false
                }
            }

            petNameValidation.apply {
                if (s?.length!! > 0) {
                    if (Pattern.compile(HANGUEL_PATTERN_NEW_FIX).matcher(s.toString()).matches()) {
                        value = true
                        petNameHelperVisible.value = true
                    } else {
                        value = false
                        petNameHelperVisible.value = false
                    }
                } else {
                    value = true
                    petNameHelperVisible.value = true
                }
            }

            petName.value = petName.value.toString().replace(" ", "")
        }
    }

    val petNameFocusChangeListener = object : View.OnFocusChangeListener {
        override fun onFocusChange(v: View?, hasFocus: Boolean) {
            val str = (v as EditText).text.toString()
            if (hasFocus) {
                petNameFocusHintVisible.value = true
                petNameLineStatus.value = true
                petNameHelperVisible.apply {
                    if (str.isNotEmpty()) {
                        value = false
                    } else {
                        value = true
                    }
                }

                petNameInputStatus.apply {
                    if (str.isNotEmpty()) {
                        value = true
                    }
                }
            } else {
                petNameFocusHintVisible.apply {
                    if (str.isNotEmpty()) {
                        value = true
                    } else {
                        value = false
                    }
                }

                petNameLineStatus.value = false
                petNameInputStatus.value = false
                petNameHelperVisible.value = false
            }
        }
    }

    val breedFocusChangeListener = object : View.OnFocusChangeListener {
        override fun onFocusChange(v: View?, hasFocus: Boolean) {
            val str = (v as EditText).text.toString()
            if (hasFocus) {
                breedFocusHintVisible.value = true
                breedLineStatus.value = true

                breedInputStatus.apply {
                    if (str.isNotEmpty()) {
                        value = true
                    }
                }
            } else {
                breedFocusHintVisible.apply {
                    if (str.isNotEmpty()) {
                        value = true
                    } else {
                        value = false
                    }
                }

                breedLineStatus.value = false
                breedInputStatus.value = false
            }
        }
    }

    val birthFocusChangeListener = object : View.OnFocusChangeListener {
        override fun onFocusChange(v: View?, hasFocus: Boolean) {
            val str = (v as EditText).text.toString()
            if (hasFocus) {
                birthFocusHintVisible.value = true
                birthLineStatus.value = true
                birthHelperVisible.apply {
                    if (str.isNotEmpty()) {
                        value = false
                    } else {
                        value = true
                    }
                }

                birthInputStatus.apply {
                    if (str.isNotEmpty()) {
                        value = true
                    }
                }
            } else {
                birthFocusHintVisible.apply {
                    if (str.isNotEmpty()) {
                        value = true
                    } else {
                        value = false
                    }
                }

                birthLineStatus.value = false
                birthInputStatus.value = false
                birthHelperVisible.value = false
            }
        }
    }

    val weighFocusChangeListener = object : View.OnFocusChangeListener {
        override fun onFocusChange(v: View?, hasFocus: Boolean) {
            val str = (v as EditText).text.toString()
            if (hasFocus) {
                weightFocusHintVisible.value = true
                weightLineStatus.value = true
                weightHelperVisible.apply {
                    if (str.isNotEmpty()) {
                        value = false
                    } else {
                        value = true
                    }
                }

                weightInputStatus.apply {
                    if (str.isNotEmpty()) {
                        value = true
                    }
                }
            } else {
                weightFocusHintVisible.apply {
                    if (str.isNotEmpty()) {
                        value = true
                    } else {
                        value = false
                    }
                }

                weightLineStatus.value = false
                weightInputStatus.value = false
                weightHelperVisible.value = false
            }
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

        petNameInputStatus.value = false
        petNameValidation.value = true
        petNameFocusHintVisible.value = false
        petNameLineStatus.value = false
        petNameHelperVisible.value = false
        breedInputStatus.value = false
        breedValidation.value = true
        breedFocusHintVisible.value = false
        breedLineStatus.value = false
        birthInputStatus.value = false
        birthValidation.value = true
        birthFocusHintVisible.value = false
        birthLineStatus.value = false
        birthHelperVisible.value = false
        weightInputStatus.value = false
        weightValidation.value = true
        weightFocusHintVisible.value = false
        weightLineStatus.value = false
        weightHelperVisible.value = false
    }

    fun onInputNameClear() {
        petName.value = ""
    }

    fun onBreedTextChanged(text: CharSequence) {

        breedInputStatus.apply {
            if (text.length > 0) {
                value = true
            } else {
                value = false
            }
        }

        breedValidation.apply {
            if (text.length > 0) {
                if (text.trim().isNotEmpty() && Pattern.compile(HANGUEL_PATTERN_ADD_SPACE).matcher(text.toString()).matches()) {
                    value = true
                } else {
                    value = false
                }
            } else {
                value = true
            }
        }
    }

    fun onInputBreedClear() {
        breed.value = ""
    }

    fun onBirthTextChanged(text: CharSequence) {

        birthInputStatus.apply {
            if (text.length > 0) {
                value = true
            } else {
                value = false
            }
        }

        birthValidation.apply {
            if (text.toString().contains("-")) {
                value = false
                birthHelperVisible.value = false
                return@apply
            }

            if (text.length == 6) {
                val current = LocalDate.now()
                val formatter = DateTimeFormatter.ofPattern("yyMMdd")
                val formatted = current.format(formatter)
                if (validationDate(text.toString()).not()
                    || Integer.parseInt(formatted) < Integer.parseInt(text.toString())) {
                    value = false
                    birthHelperVisible.value = false
                } else {
                    value = true
                    birthHelperVisible.value = true
                }
            } else {
                value = true
                birthHelperVisible.value = true
            }
        }
    }

    fun onInputBirthClear() {
        birth.value = ""
    }

    fun onWeightTextChanged(text: CharSequence) {

        weightInputStatus.apply {
            if (text.length > 0) {
                value = true
            } else {
                value = false
            }
        }

        weightValidation.apply {
            if (text.length > 0) {
                if(text.toString() != "." && Pattern.compile(NUMBER_DOT_PATTERN).matcher(text.toString()).matches()) {
                    value = true
                    weightHelperVisible.value = true
                } else {
                    value = false
                    weightHelperVisible.value = false
                }
            } else {
                value = true
                weightHelperVisible.value = true
            }
        }
    }

    fun onInputWeighClear() {
        weight.value = ""
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
                return false
            }

            val current = LocalDate.now()
            val formatter = DateTimeFormatter.ofPattern("yyMMdd")
            val formatted = current.format(formatter)
            if (validationDate(birth.value.toString()).not()
                || Integer.parseInt(formatted) < Integer.parseInt(birth.value.toString())) {
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