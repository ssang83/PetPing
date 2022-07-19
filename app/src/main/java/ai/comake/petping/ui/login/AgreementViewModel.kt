package ai.comake.petping.ui.login

import ai.comake.petping.*
import ai.comake.petping.api.Resource
import ai.comake.petping.data.repository.LoginRepository
import ai.comake.petping.data.vo.AgreementConfig
import ai.comake.petping.data.vo.ErrorResponse
import ai.comake.petping.data.vo.PolicyData
import ai.comake.petping.util.getErrorBodyConverter
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.ab180.airbridge.Airbridge
import co.ab180.airbridge.event.StandardEventCategory
import co.ab180.airbridge.event.model.SemanticAttributes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * android-petping-2
 * Class: AgreementViewModel
 * Created by cliff on 2022/03/11.
 *
 * Description:
 */
@HiltViewModel
class AgreementViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var loginRepository: LoginRepository

    val allAgreement = MutableLiveData<Boolean>().apply { value = false }
    val serviceAgreement = MutableLiveData<Boolean>().apply { value = false }
    val locationAgreement = MutableLiveData<Boolean>().apply { value = false }
    val privacyAgreement = MutableLiveData<Boolean>().apply { value = false }
    val ageAgreement = MutableLiveData<Boolean>().apply { value = false }
    val marketingAgreement = MutableLiveData<Boolean>().apply { value = false }

    val moveToPolicyPage = MutableLiveData<Event<String>>()
    val moveToHome = MutableLiveData<Event<Unit>>()
    val moveToLogin = MutableLiveData<Event<Unit>>()
    val uiState = MutableLiveData<Event<UiState>>()
    val emailAuthNeedPopup = MutableLiveData<Event<Unit>>()
    val emailRegisterPopup = MutableLiveData<Event<Unit>>()
    val loginErrorPopup = MutableLiveData<Event<ErrorResponse>>()

    var policyList: List<PolicyData> = listOf()
    var config:AgreementConfig? = null

    fun loadData(args: AgreementFragmentArgs) {
        config = args.config

        viewModelScope.launch {
            uiState.emit(UiState.Loading)
            val response = loginRepository.getPolicies(SAPA_KEY)
            when (response) {
                is Resource.Success -> {
                    uiState.emit(UiState.Success)
                    policyList = response.value.data
                }
                else -> uiState.emit(UiState.Failure(null))
            }
        }
    }

    /**
     * 가입 완료 버튼 클릭 시 서버 체크
     * 성공 시 인증 메일 발송 안내 팝업
     * 만 14세 미만이거나 가입 이력이 있으면 에러 팝업
     */
    fun signUp(context: Context) = viewModelScope.launch {
        uiState.emit(UiState.Loading)
        val body = makeSignUpBody(
            config?.emailKey ?: "",
            config?.signUpType ?: 1,
            config?.authWord ?: "",
            AppConstants.getAndroidId(context),
            config?.snsToken ?: "",
            serviceAgreement.value!!,
            locationAgreement.value!!,
            privacyAgreement.value!!,
            marketingAgreement.value!!,
            ageAgreement.value!!
        )
        val response = loginRepository.requestSignUp(SAPA_KEY, body)
        when (response) {
            is Resource.Success -> {
                uiState.emit(UiState.Success)
                AppConstants.ID = response.value.data.id
                AppConstants.AUTH_KEY = "Bearer ${response.value.data.authorizationToken}"
                AppConstants.EMAIL = response.value.data.email

                if (config?.signUpType == 1) { // 이메일 가입
                    if (response.value.data.isEmailAuthSend) {
                        emailAuthNeedPopup.emit()
                    } else {
                        moveToHome.emit()
                    }
                } else if (config?.signUpType == 2) { // 네이버 가입
                    moveToHome.emit()
                } else if(config?.signUpType == 3) { // 카카오 가입
                    if (response.value.data.isEmailAuthSend) {
                        emailAuthNeedPopup.emit()
                    } else if (response.value.data.email.isNullOrEmpty()) {
                        emailRegisterPopup.emit()
                    } else {
                        moveToHome.emit()
                    }
                } else { // 애플 기입
                    moveToHome.emit()
                }

                // airbridge
                Airbridge.getCurrentUser().setAlias(
                    "FLAG_SEND_AUTHENTICATION_EMAIL",
                    config?.sendAuthMailFlag.toString()
                )
                Airbridge.getCurrentUser().id = response.value.data.id
                Airbridge.getCurrentUser().email = response.value.data.email
                Airbridge.getCurrentUser()
                    .setAlias("DEVICE_ID", AppConstants.getAndroidId(context))
                Airbridge.getCurrentUser().setAlias(
                    "SIGN_UP_TYPE",
                    config?.signUpType.toString()
                )
                Airbridge.getCurrentUser()
                    .setAlias("AUTH_WORD", config?.authWord.toString())
                Airbridge.getCurrentUser().setAlias(
                    "MKT_AGREEMENT",
                    marketingAgreement.value.toString()
                )
                // airbridge sign up event
                Airbridge.getCurrentUser().apply {
                    id = response.value.data.id
                    email = response.value.data.email
                    phone = ""
                    setAlias("", "")
                    setAttribute("", "")
                }

                val event = co.ab180.airbridge.event.Event(StandardEventCategory.SIGN_UP)
                Airbridge.trackEvent(event)

                // airbridge sign up event
                val eventValue = 10f
                val eventAttributes = mutableMapOf<String, String>()
                val semanticAttributes = SemanticAttributes()
                Airbridge.trackEvent(
                    "signup_event",
                    "signup_action",
                    "signup_label",
                    eventValue,
                    eventAttributes,
                    semanticAttributes.toMap()
                )
            }
            is Resource.Failure -> {
                uiState.emit(UiState.Failure(null))
                response.errorBody?.let { errorBody ->
                    val errorResponse = getErrorBodyConverter().convert(errorBody)!!
                    loginErrorPopup.emit(errorResponse)
                }
            }
        }
    }

    /**
     * 모두 동의합니다 체크에 따라 아래 항목 체크 or 체크 해제
     */
    fun onAllAgreementChanged(checked: Boolean) {
        serviceAgreement.value = checked
        locationAgreement.value = checked
        privacyAgreement.value = checked
        ageAgreement.value = checked
        marketingAgreement.value = checked
    }

    /**
     * 보기 클릭 시 약관 본문 웹뷰로 이동
     */
    fun goToPolicyPage(type: Int) {
        val url = policyList.find { policyData ->
            policyData.type == type
        }?.policyURL
        url?.let { moveToPolicyPage.emit(it) }
    }

    /**
     * 5개 모두 체크되면 모두 동의합니다 체크
     */
    fun onAgreementChanged(checked: Boolean) {
        if (!checked) {
            allAgreement.value = false
            return
        }

        if (serviceAgreement.value!! && locationAgreement.value!! && privacyAgreement.value!! && ageAgreement.value!! && marketingAgreement.value!!){
            allAgreement.value = true
        }
    }
}