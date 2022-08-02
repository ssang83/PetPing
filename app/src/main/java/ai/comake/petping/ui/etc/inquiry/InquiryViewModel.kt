package ai.comake.petping.ui.etc.inquiry

import ai.comake.petping.*
import ai.comake.petping.api.Resource
import ai.comake.petping.data.repository.UserDataRepository
import ai.comake.petping.data.vo.InquiryType
import ai.comake.petping.util.Coroutines
import ai.comake.petping.util.getBitmapFromInputStream
import android.content.Context
import android.net.Uri
import android.os.Build
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import javax.inject.Inject

/**
 * android-petping-2
 * Class: InquiryViewModel
 * Created by cliff on 2022/06/28.
 *
 * Description:
 */
@HiltViewModel
class InquiryViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var repository: UserDataRepository

    private val _inquirySubject = MutableStateFlow("")
    val inquirySubject get() = _inquirySubject

    private val _content = MutableStateFlow("")
    val content get() = _content

    private val _inquiryType = MutableStateFlow("")
    val inquiryType get() = _inquiryType

    private val _subjectInputStatus = MutableStateFlow(false)
    val subjectInputStatus get() = _subjectInputStatus

    private val _contentInputStatus = MutableStateFlow(false)
    val contentInputStatus get() = _contentInputStatus

    private val _typeInputStatus = MutableStateFlow(false)
    val typeInputStatus get() = _typeInputStatus

    private val _isScroll = MutableLiveData<Boolean>().apply { value = false }
    val isScroll: LiveData<Boolean> get() = _isScroll

    val imageMargin = MutableStateFlow(false)
    val isBottomBtnShow = MutableStateFlow(false)

    // Event
    private val _eventFlow = MutableEventFlow<InquiryEvent>()
    val eventFlow = _eventFlow.asEventFlow()

    private val _uiState = MutableSharedFlow<UiState>()
    val uiState get() = _uiState.asSharedFlow()

    val imageList = arrayListOf<String>()
    var inquiryTypes: List<InquiryType> = listOf()
    var inquiryList = arrayListOf<String>()

    val scrollChangeListener = object : View.OnScrollChangeListener {
        override fun onScrollChange(
            v: View?,
            scrollX: Int,
            scrollY: Int,
            oldScrollX: Int,
            oldScrollY: Int
        ) {
            if (v?.scrollY == 0) {
                _isScroll.value = false
            } else {
                _isScroll.value = true
            }
        }
    }

    fun getType() = Coroutines.main(this) {
        _uiState.emit(UiState.Loading)
        val response = repository.getInquiryType(AppConstants.AUTH_KEY)
        when (response) {
            is Resource.Success -> {
                _uiState.emit(UiState.Success)
                inquiryTypes = response.value.data
                inquiryTypes.forEach { inquiryList.add(it.inquiryTypeStr) }
            }
            else -> _uiState.emit(UiState.Failure(null))
        }
    }

    fun uploadInquiry(context: Context) = Coroutines.main(this) {
        _uiState.emit(UiState.Loading)
        val type = inquiryTypes.find { it.inquiryTypeStr == _inquiryType.value }?.inquiryType ?: 0
        val title = _inquirySubject.value
        val content = _content.value

        val body = ArrayList<MultipartBody.Part>()
        imageList.forEachIndexed { index, s ->
            val contents = try {
                context.contentResolver.openInputStream(Uri.parse(s))
            } catch (e: FileNotFoundException) {
                FileInputStream(File(s))
            }

            val outputStream = getBitmapFromInputStream(contents!!, context, s)
            val filePart = MultipartBody.Part.createFormData(
                "file", "${index}.png", outputStream.toByteArray().toRequestBody(
                    "image/png".toMediaTypeOrNull(), 0,
                    outputStream.size()
                )
            )

            body.add(filePart)
        }

        val response = repository.uploadPersonalInquirys(
            AppConstants.AUTH_KEY,
            MultipartBody.Part.createFormData("memberId", AppConstants.ID),
            MultipartBody.Part.createFormData("inquiryType", type.toString()),
            MultipartBody.Part.createFormData("title", title),
            MultipartBody.Part.createFormData("content", content),
            MultipartBody.Part.createFormData("appVersion", BuildConfig.VERSION_NAME),
            MultipartBody.Part.createFormData("osVersion", Build.VERSION.RELEASE),
            MultipartBody.Part.createFormData("deviceInfo", Build.MODEL),
            MultipartBody.Part.createFormData("mobileCarrierInfo", "temp"),
            MultipartBody.Part.createFormData("deviceId", AppConstants.UUID),
            body
        )

        when (response) {
            is Resource.Success -> {
                _uiState.emit(UiState.Success)
                event(InquiryEvent.CloseScreen)
            }
            else -> _uiState.emit(UiState.Failure(null))
        }
    }

    fun removeImage(position: Int) {
        imageList.removeAt(position)
        event(InquiryEvent.UploadImage)
    }

    fun onInqurySubjectTextChanged(subject: CharSequence) {
        _subjectInputStatus.apply {
            if (subject.length > 0) {
                value = true
            } else {
                value = false
            }
        }
    }

    fun onContentTextChanged(content: CharSequence) {
        _contentInputStatus.apply {
            if (content.length > 0) {
                value = true
            } else {
                value = false
            }
        }
    }

    fun pickPicture() {
        event(InquiryEvent.PickPicture)
    }

    private fun event(event: InquiryEvent) {
        Coroutines.main(this) {
            _eventFlow.emit(event)
        }
    }

    sealed class InquiryEvent {
        object CloseScreen : InquiryEvent()
        object PickPicture : InquiryEvent()
        object UploadImage : InquiryEvent()
    }
}