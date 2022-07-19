package ai.comake.petping.ui.login

import ai.comake.petping.*
import ai.comake.petping.api.Resource
import ai.comake.petping.data.repository.LoginRepository
import ai.comake.petping.data.vo.ErrorResponse
import ai.comake.petping.data.vo.NaverData
import ai.comake.petping.data.vo.NaverResponse
import ai.comake.petping.util.LogUtil
import ai.comake.petping.util.getErrorBodyConverter
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.GsonBuilder
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.kakao.sdk.user.model.User
import com.nhn.android.naverlogin.OAuthLogin
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * android-petping-2
 * Class: LoginViewModel
 * Created by cliff on 2022/03/10.
 *
 * Description:
 */
@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var loginRepository: LoginRepository

    val moveToEmailLogin = MutableLiveData<Event<Unit>>()
    val moveToNaverLogin = MutableLiveData<Event<Unit>>()
    val moveToKakaoLogin = MutableLiveData<Event<Unit>>()
    val moveToAppleLogin = MutableLiveData<Event<Unit>>()
    val naverLoginError = MutableLiveData<Event<ErrorResponse>>()
    val kakaoLoginError = MutableLiveData<Event<ErrorResponse>>()
    val appleLoginError = MutableLiveData<Event<ErrorResponse>>()
    val outLinkFacebook = MutableLiveData<Event<String>>()
    val outLinkInsta = MutableLiveData<Event<String>>()
    val outLinkNaver = MutableLiveData<Event<String>>()
    val moveToHome = MutableLiveData<Event<Unit>>()
    val uiState = MutableLiveData<Event<UiState>>()

    var naverUserData:NaverData? = null
    var naverToken = ""
    var kakaoUserData:User? = null
    var kakaoToken = ""

    fun getKakaoUserInfo(context: Context, token: OAuthToken) {
        // 사용자 정보 요청 (기본)
        UserApiClient.instance.me { user, error ->
            if (error != null) {
                LogUtil.log("사용자 정보 요청 실패 $error")
            } else if (user != null) {
                LogUtil.log(
                    "사용자 정보 요청 성공" +
                            "\n회원번호: ${user.id}" +
                            "\n이메일: ${user.kakaoAccount?.email}" +
                            "\n닉네임: ${user.kakaoAccount?.profile?.nickname}" +
                            "\n프로필사진: ${user.kakaoAccount?.profile?.thumbnailImageUrl}"
                )

                kakaoUserData = user
                kakaoToken = token.accessToken

                // 로그인 , 회원가입 진행
                loginKakao(context, user, token)
            }
        }
    }

    /**
     * 네이버 로그인 , 회원가입 진행
     */
    fun loginNaver(context: Context, naverData: NaverData, token: String?) = viewModelScope.launch {
        uiState.emit(UiState.Loading)
        val body = makeLoginBody(
            "2",
            naverData.email ?: "",
            naverData.id ?: "",
            AppConstants.getAndroidId(context),
            token ?: ""
        )
        val response = loginRepository.requestSignIn(SAPA_KEY, body)
        LogUtil.log("TAG","response $response")
        when (response) {
            is Resource.Success -> {
                uiState.emit(UiState.Success)
                AppConstants.ID = response.value.data.id
                AppConstants.AUTH_KEY = "Bearer ${response.value.data.authorizationToken}"
                AppConstants.LOGIN_HEADER_IS_VISIBLE = false
                moveToHome.emit()
            }
            is Resource.Failure -> {
                uiState.emit(UiState.Failure(null))
                response.errorBody?.let { errorBody ->
                    val errorResponse = getErrorBodyConverter().convert(errorBody)!!
                    naverLoginError.emit(errorResponse)
                }
            }
        }
    }

    /**
     * 카카오 로그인 , 회원가입 진행
     */
    fun loginKakao(context: Context, user: User, token: OAuthToken) = viewModelScope.launch {
        uiState.emit(UiState.Loading)
        val body = makeLoginBody(
            "3",
            user.kakaoAccount?.email ?: "",
            user.id.toString(),
            AppConstants.getAndroidId(context),
            token.accessToken
        )
        val response = loginRepository.requestSignIn(SAPA_KEY, body)
        when (response) {
            is Resource.Success -> {
                uiState.emit(UiState.Success)
                AppConstants.ID = response.value.data.id
                AppConstants.AUTH_KEY = "Bearer ${response.value.data.authorizationToken}"
                AppConstants.LOGIN_HEADER_IS_VISIBLE = false
                moveToHome.emit()
            }
            is Resource.Failure -> {
                uiState.emit(UiState.Failure(null))
                response.errorBody?.let { errorBody ->
                    val errorResponse = getErrorBodyConverter().convert(errorBody)!!
                    kakaoLoginError.emit(errorResponse)
                }
            }
        }
    }

    /**
     * 애플로그인
     *
     * @param token
     */
    fun loginApple(context: Context, token: String) = viewModelScope.launch {
        uiState.emit(UiState.Loading)
        val body = makeLoginBody(
            "4",
            "",
            "",
            AppConstants.getAndroidId(context),
            token
        )
        val response = loginRepository.requestSignIn(SAPA_KEY, body)
        when (response) {
            is Resource.Success -> {
                uiState.emit(UiState.Success)
                AppConstants.ID = response.value.data.id
                AppConstants.AUTH_KEY = "Bearer ${response.value.data.authorizationToken}"
                AppConstants.LOGIN_HEADER_IS_VISIBLE = false
                moveToHome.emit()
            }
            is Resource.Failure -> {
                uiState.emit(UiState.Failure(null))
                response.errorBody?.let { errorBody ->
                    val errorResponse = getErrorBodyConverter().convert(errorBody)!!
                    appleLoginError.emit(errorResponse)
                }
            }
        }
    }

    fun goToNaverLogin() {
        moveToNaverLogin.emit()
    }

    fun goToKakaoLogin() {
        moveToKakaoLogin.emit()
    }

    fun goToAppleLogin() {
        moveToAppleLogin.emit()
    }

    fun goToFacebookUrl() {
        outLinkFacebook.emit("https://abit.ly/petping-fb-app-footer")
    }

    fun goToInstagramUrl() {
        outLinkInsta.emit("https://abit.ly/petping-insta-app-footer")
    }

    fun goToNaverUrl() {
        outLinkNaver.emit("https://abit.ly/petping-blog-app-footer")
    }

    fun goToOtherLogin() {
        moveToEmailLogin.emit()
    }
}