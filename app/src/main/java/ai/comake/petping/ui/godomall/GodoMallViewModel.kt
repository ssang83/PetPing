package ai.comake.petping.ui.godomall

import ai.comake.petping.*
import ai.comake.petping.api.Resource
import ai.comake.petping.data.repository.AppDataRepository
import ai.comake.petping.data.repository.ShopRepository
import ai.comake.petping.data.vo.ErrorResponse
import ai.comake.petping.ui.common.webview.GodoMallWebView
import ai.comake.petping.util.LogUtil
import ai.comake.petping.util.getErrorBodyConverter
import ai.comake.petping.util.toNumberFormat
import android.net.Uri
import android.webkit.JsResult
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * android-petping-2
 * Class: GodoMallViewModel
 * Created by cliff on 2022/02/09.
 *
 * Description:
 */
@HiltViewModel
class GodoMallViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var appDataRepository: AppDataRepository

    @Inject
    lateinit var shopRepository: ShopRepository

    private val _pingAmount = MutableLiveData<String>()
    val pingAmount:LiveData<String>
        get() = _pingAmount

    private val _refreshAnimation = MutableLiveData<Boolean>()
    val refreshAnimation:LiveData<Boolean>
        get() = _refreshAnimation

    private val _pingErrorPopup = MutableLiveData<Event<ErrorResponse>>()
    val pingErrorPopup:LiveData<Event<ErrorResponse>>
        get() = _pingErrorPopup

    private val _moveToLogin = MutableLiveData<Event<Unit>>()
    val moveToLogin:LiveData<Event<Unit>>
        get() = _moveToLogin

    private val _loadingPage = MutableLiveData<Event<Unit>>()
    val loadingPage:LiveData<Event<Unit>>
        get() = _loadingPage

    private val _accessTokenExpire = MutableLiveData<Event<Unit>>()
    val accessTokenExpire:LiveData<Event<Unit>>
        get() = _accessTokenExpire

    // WebView 관련 변수
    val showJsAlert = MutableLiveData<Event<JSAlertConfig>>()
    val showJsConfirm = MutableLiveData<Event<JSConfirmConfig>>()
    val showFileChooser = MutableLiveData<Event<FileChooserConfig>>()

    val webViewListener = object : GodoMallWebView.GodoMallWebViewListener {
        override fun goToLogin() {
            viewModelScope.launch {
                val response = appDataRepository.getAccessTokenTime(AppConstants.AUTH_KEY)
                when (response) {
                    is Resource.Success -> {
                        val currenTime = Date(System.currentTimeMillis())
                        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA)
                        val expireTime = sdf.parse(response.value.data.tokenExpireAt)
                        expireTime?.let { it ->
                            if (it.time > currenTime.time) {
                                _accessTokenExpire.emit()
                            } else {
                                _moveToLogin.emit()
                            }
                        }
                    }
                    is Resource.Failure -> {
                        _moveToLogin.emit()
                    }
                }
            }
        }
    }

    fun getPingPoint(isFirst:Boolean) = viewModelScope.launch {
        _refreshAnimation.value = isFirst
        val response = shopRepository.getPingPoint(AppConstants.AUTH_KEY, AppConstants.ID)
        when (response) {
            is Resource.Success -> {
                _pingAmount.value = response.value.data.reward.toString().toNumberFormat()
            }
            is Resource.Failure -> {
                response.errorBody?.let { errorBody ->
                    val errorResponse = getErrorBodyConverter().convert(errorBody)!!
                    _pingErrorPopup.emit(errorResponse)
                }
            }
        }
    }

    fun shopLogin() = viewModelScope.launch {
        val body = makeShopLoginBody()
        val flag = if(BuildConfig.DEBUG) "always" else "clear"
        val mode = if(BuildConfig.DEBUG) "develop" else "master"
        val response = shopRepository.shopLogin("XMLHttpRequest", flag, mode, body)
        when (response) {
            is Resource.Success -> {
                LogUtil.log("TAG", "ssessionKeyName : ${response.value.data.sessionKeyName}")
                LogUtil.log("TAG", "sessionKeyValue : ${response.value.data.sessionKeyValue}")
                AppConstants.SHOP_SESSION_NAME = response.value.data.sessionKeyName
                AppConstants.SHOP_SESSION_KEY = response.value.data.sessionKeyValue

                getPingPoint(true)
                _loadingPage.emit()
            }
        }
    }
}

data class JSAlertConfig(
    val message:String?,
    val result: JsResult?
)

data class JSConfirmConfig(
    val message:String?,
    val result: JsResult?
)

data class FileChooserConfig(
    val callback: ValueCallback<Array<Uri>>?,
    val params: WebChromeClient.FileChooserParams?
)