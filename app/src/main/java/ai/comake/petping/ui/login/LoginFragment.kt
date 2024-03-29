package ai.comake.petping.ui.login

import ai.comake.petping.*
import ai.comake.petping.data.vo.AgreementConfig
import ai.comake.petping.data.vo.AppleLoginConfig
import ai.comake.petping.data.vo.NaverData
import ai.comake.petping.databinding.FragmentLoginBinding
import ai.comake.petping.ui.base.BaseFragment
import ai.comake.petping.ui.common.dialog.PermissionDialog
import ai.comake.petping.ui.common.dialog.SingleBtnDialog
import ai.comake.petping.util.LogUtil
import ai.comake.petping.util.SharedPreferencesManager
import ai.comake.petping.util.backStack
import ai.comake.petping.util.updateWhiteStatusBar
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.kakao.sdk.auth.LoginClient
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthBehavior
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import com.nhn.android.naverlogin.OAuthLogin
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * android-petping-2
 * Class: LoginFragment
 * Created by cliff on 2022/03/10.
 *
 * Description:
 */
@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {

    @Inject
    lateinit var prefs: SharedPreferencesManager

    private val viewModel: LoginViewModel by viewModels()
    private val mainShareViewModel: MainShareViewModel by activityViewModels()

    var appleLoginConfig:AppleLoginConfig? = null

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            finishApplication()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateWhiteStatusBar(requireActivity().window)
        requireActivity().onBackPressedDispatcher.addCallback(onBackPressedCallback)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        if (prefs.getAuthorityPopup().not()) {
            PermissionDialog(requireContext()).show()
            prefs.setAuthorityPopup(true)
        }

        with(viewModel) {

            getLoginStatus()

            uiState.observeEvent(viewLifecycleOwner) { state ->
                when (state) {
                    is UiState.Loading -> mainShareViewModel.showPopUp.emit(true)
                    else -> mainShareViewModel.showPopUp.emit(false)
                }
            }

            moveToHome.observeEvent(viewLifecycleOwner) {
                mainShareViewModel.registFCMToken()
                requireActivity().findNavController(R.id.nav_main).navigate(R.id.action_global_homeScreen)
//                requireActivity().backStack(R.id.nav_main)
            }

            moveToEmailLogin.observeEvent(viewLifecycleOwner) {
                requireActivity().findNavController(R.id.nav_main)
                    .navigate(R.id.action_loginFragment_to_emailLoginFragment)
            }

            moveToNaverLogin.observeEvent(viewLifecycleOwner) {
                callNaverLogin(requireContext())

//                //테스트 로그아웃
//                NaverIdLoginSDK.logout()
//                //테스트 로그인 연동해제
//                callNaverDeleteToken(requireContext())
            }

            moveToKakaoLogin.observeEvent(viewLifecycleOwner) {
                kakaoLogin()
            }

            moveToAppleLogin.observeEvent(viewLifecycleOwner) {
                AppleSignInDialog(
                    requireContext(),
                    callBack = { config ->
                        LogUtil.log("apple token : ${config.authWord}, email : ${config.email}, snsAuthToken : ${config.snsAuthToken}")
                        appleLoginConfig = config
                        loginApple(requireContext(), config)
                    }
                ).show()
            }

            naverLoginError.observeEvent(viewLifecycleOwner) { errorBody ->
                when (errorBody.code) {
                    "C1090", "C1060" -> {
                        val config = AgreementConfig(
                            snsToken = naverToken,
                            authWord = naverUserData?.id.toString(),
                            signUpType = 2,
                            emailKey = naverUserData?.email ?: "",
                            nickName = naverUserData?.nickname ?: "",
                            sendAuthMailFlag = false
                        )

                        requireActivity().findNavController(R.id.nav_main).navigate(
                            LoginFragmentDirections.actionLoginFragmentToAgreementFragment(config)
                        )
                    }
                    else -> {
                        SingleBtnDialog(
                            requireContext(),
                            getString(R.string.login_error),
                            errorBody.message
                        ) {}.show()
                    }
                }
            }

            kakaoLoginError.observeEvent(viewLifecycleOwner) { errorBody ->
                when (errorBody.code) {
                    "C1090", "C1060" -> {
                        val config = AgreementConfig(
                            snsToken = kakaoToken,
                            authWord = kakaoUserData?.id.toString(),
                            signUpType = 3,
                            emailKey = kakaoUserData?.kakaoAccount?.email ?: "",
                            nickName = kakaoUserData?.kakaoAccount?.profile?.nickname ?: "",
                            sendAuthMailFlag = false
                        )

                        requireActivity().findNavController(R.id.nav_main).navigate(
                            LoginFragmentDirections.actionLoginFragmentToAgreementFragment(config)
                        )
                    }
                    else -> {
                        SingleBtnDialog(
                            requireContext(),
                            getString(R.string.login_error),
                            errorBody.message
                        ) {}.show()
                    }
                }
            }

            appleLoginError.observeEvent(viewLifecycleOwner) { errorBody ->
                when (errorBody.code) {
                    "C1090", "C1060" -> {
                        val config = AgreementConfig(
                            snsToken = appleLoginConfig!!.snsAuthToken,
                            authWord = appleLoginConfig!!.authWord,
                            signUpType = 4,
                            emailKey = appleLoginConfig!!.email,
                            nickName = "",
                            sendAuthMailFlag = false
                        )

                        requireActivity().findNavController(R.id.nav_main).navigate(
                            LoginFragmentDirections.actionLoginFragmentToAgreementFragment(config)
                        )
                    }
                    else -> {
                        SingleBtnDialog(
                            requireContext(),
                            getString(R.string.login_error),
                            errorBody.message
                        ) {}.show()
                    }
                }
            }

            outLinkFacebook.observeEvent(viewLifecycleOwner) { url ->
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                requireActivity().startActivity(intent)
            }

            outLinkInsta.observeEvent(viewLifecycleOwner) { url ->
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                requireActivity().startActivity(intent)
            }

            outLinkNaver.observeEvent(viewLifecycleOwner) { url ->
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                requireActivity().startActivity(intent)
            }
        }
    }

    override fun onDestroyView() {
        onBackPressedCallback.remove()
        super.onDestroyView()
    }

    private fun kakaoLogin() {
        val kakaoCallback: (token: OAuthToken?, error: Throwable?) -> Unit = { token, error ->
            if (error != null) {
                if (LoginClient.instance.isKakaoTalkLoginAvailable(requireContext())) {
                    // 카카오톡설치됨 로그인안됨
                    LogUtil.log("로그인 실패 카카오톡설치됨 로그인안됨 $error")
                    val kakaoAccountCallback: (token: OAuthToken?, error: Throwable?) -> Unit =
                        { token, error ->
                            if (error != null) {
                                LogUtil.log("로그인 실패 $error")
                            } else if (token != null) {
                                LogUtil.log("로그인 성공 ${token.accessToken}")
                                UserApiClient.instance.me { user, error ->
                                    viewModel.kakaoUserData = user
                                    viewModel.kakaoToken = token.accessToken
                                    if (error != null) {
                                        LogUtil.log("엑세스 토큰 정보 읽기 실패 $error")
                                    } else {
                                        viewModel.loginKakao(requireContext(), user!!, token)
                                    }
                                }
                            }
                        }
                    LoginClient.instance.loginWithKakaoAccount(
                        requireContext(),
                        callback = kakaoAccountCallback
                    )
                }
                LogUtil.log("로그인 실패 $error")
            } else if (token != null) {
                LogUtil.log("로그인 성공 ${token.accessToken}")
                viewModel.getKakaoUserInfo(requireContext(), token)
            }
        }

        if (LoginClient.instance.isKakaoTalkLoginAvailable(requireContext())) {
            LoginClient.instance.loginWithKakaoTalk(requireContext(), callback = kakaoCallback)
        } else {
            LoginClient.instance.loginWithKakaoAccount(requireContext(), callback = kakaoCallback)
        }
    }

    fun callNaverLogin(context: Context) {
        LogUtil.log("TAG", " " + context.packageName)
        NaverIdLoginSDK.behavior = NidOAuthBehavior.DEFAULT
        NaverIdLoginSDK.authenticate(context, object : OAuthLoginCallback {
            override fun onSuccess() {
                callNaverProfile()
            }

            override fun onFailure(httpStatus: Int, message: String) {
                val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                Toast.makeText(
                    context,
                    "errorCode:$errorCode, errorDesc:$errorDescription",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onError(errorCode: Int, message: String) {
                onFailure(errorCode, message)
            }
        })
    }

    fun callNaverProfile() {
        NidOAuthLogin().callProfileApi(object : NidProfileCallback<NidProfileResponse> {
            override fun onSuccess(response: NidProfileResponse) {
                val tvAccessToken = NaverIdLoginSDK.getAccessToken()
                LogUtil.log("TAG", "tvAccessToken $tvAccessToken")
                val id = response.profile?.id
                val nickname = response.profile?.nickname
                val email = response.profile?.email
                val profileImage = response.profile?.profileImage
                val naverData = NaverData(id, nickname, profileImage, email)

                LogUtil.log("TAG", "id $id")
                LogUtil.log("TAG", "nickname $nickname")
                LogUtil.log("TAG", "email $email")

                viewModel.loginNaver(requireContext(), naverData, tvAccessToken)
            }

            override fun onFailure(httpStatus: Int, message: String) {
                val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                Toast.makeText(
                    context,
                    "errorCode:$errorCode, errorDesc:$errorDescription",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onError(errorCode: Int, message: String) {
                onFailure(errorCode, message)
            }
        })
    }

    fun callNaverDeleteToken(context: Context) {
        NidOAuthLogin().callDeleteTokenApi(context, object : OAuthLoginCallback {
            override fun onSuccess() {
                LogUtil.log("TAG", "")
            }

            override fun onFailure(httpStatus: Int, message: String) {
                val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                Toast.makeText(
                    context,
                    "errorCode:$errorCode, errorDesc:$errorDescription",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onError(errorCode: Int, message: String) {
                onFailure(errorCode, message)
            }
        })
    }

    /**
     * '뒤로가기' 버튼 2회 연속 입력을 통한 종료를 사용자에게 안내하고 처리
     */
    private var backPressedTime: Long = 0
    private fun finishApplication() {
        if (System.currentTimeMillis() - backPressedTime < AppConstants.DOUBLE_BACK_PRESS_EXITING_TIME_LIMIT) {
            requireActivity().finish()
            return
        }
        Toast.makeText(activity, getString(R.string.finish_app_guide), Toast.LENGTH_SHORT).show()
        backPressedTime = System.currentTimeMillis()
    }
}