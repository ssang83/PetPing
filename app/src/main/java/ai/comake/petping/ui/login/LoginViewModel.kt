package ai.comake.petping.ui.login

import ai.comake.petping.*
import ai.comake.petping.AppConstants.SAPA_KEY
import ai.comake.petping.api.Resource
import ai.comake.petping.data.repository.LoginRepository
import ai.comake.petping.data.vo.AppleLoginConfig
import ai.comake.petping.data.vo.ErrorResponse
import ai.comake.petping.data.vo.NaverData
import ai.comake.petping.data.vo.UserDataStore
import ai.comake.petping.util.LogUtil
import ai.comake.petping.util.SharedPreferencesManager
import ai.comake.petping.util.getErrorBodyConverter
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.kakao.sdk.user.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
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
    lateinit var sharedPreferencesManager: SharedPreferencesManager

    @Inject
    lateinit var loginRepository: LoginRepository

    private val _loginType = MutableLiveData<Int>().apply { value = -1 }
    val loginType : LiveData<Int> get() = _loginType

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

    fun getLoginStatus() {
        _loginType.value = sharedPreferencesManager.getLoginType()
    }

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

                sharedPreferencesManager.saveLoginDataStore(UserDataStore(AppConstants.AUTH_KEY,AppConstants.ID))
                sharedPreferencesManager.saveLoginType(2)

                moveToHome.emit()
            }
            is Resource.Error -> {
                uiState.emit(UiState.Failure(null))
                response.errorBody?.let { errorBody ->
                    naverLoginError.emit(errorBody)
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

                sharedPreferencesManager.saveLoginDataStore(UserDataStore(AppConstants.AUTH_KEY,AppConstants.ID))
                sharedPreferencesManager.saveLoginType(3)

                moveToHome.emit()
            }
            is Resource.Error -> {
                uiState.emit(UiState.Failure(null))
                response.errorBody?.let { errorBody ->
                    kakaoLoginError.emit(errorBody)
                }
            }
        }
    }

    /**
     * 애플로그인
     *
     * @param token
     */
    fun loginApple(context: Context, config: AppleLoginConfig) = viewModelScope.launch {
        uiState.emit(UiState.Loading)
        val body = makeLoginBody(
            "4",
            config.email,
            config.authWord,
            AppConstants.getAndroidId(context),
            config.snsAuthToken
        )
        val response = loginRepository.requestSignInV2(SAPA_KEY, body)
        when (response) {
            is Resource.Success -> {
                uiState.emit(UiState.Success)
                if (response.value.status == "200") {
                    AppConstants.ID = response.value.data.id
                    AppConstants.AUTH_KEY = "Bearer ${response.value.data.authorizationToken}"

                    sharedPreferencesManager.saveLoginDataStore(UserDataStore(AppConstants.AUTH_KEY,AppConstants.ID))
                    sharedPreferencesManager.saveLoginType(4)

                    moveToHome.emit()
                } else {
                    appleLoginError.emit(response.value.error)
                }
            }
            is Resource.Error -> {
                uiState.emit(UiState.Failure(null))
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