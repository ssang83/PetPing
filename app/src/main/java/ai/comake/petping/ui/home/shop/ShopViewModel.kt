package ai.comake.petping.ui.home.shop

import ai.comake.petping.AppConstants
import ai.comake.petping.Event
import ai.comake.petping.api.Resource
import ai.comake.petping.data.repository.ShopRepository
import ai.comake.petping.data.vo.ErrorResponse
import ai.comake.petping.data.vo.RecommendGoods
import ai.comake.petping.emit
import ai.comake.petping.util.LogUtil
import ai.comake.petping.util.getErrorBodyConverter
import ai.comake.petping.util.toNumberFormat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShopViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var shopRepository: ShopRepository

    private val _pingAmount = MutableLiveData<String>()
    val pingAmount:LiveData<String>
        get() = _pingAmount

    private val _pingShopItems = MutableLiveData<List<RecommendGoods>>()
    val pingShopItems:LiveData<List<RecommendGoods>>
        get() = _pingShopItems

    private val _petName = MutableLiveData<String>()
    val petName:LiveData<String>
        get() = _petName

    private val _scrollTopFlag = MutableLiveData<Boolean>().apply { value = false }
    val scrollTopFlag:LiveData<Boolean>
        get() = _scrollTopFlag

    private val _topBtnVisible = MutableLiveData<Boolean>().apply { value = false }
    val topBtnVisible:LiveData<Boolean>
        get() = _topBtnVisible

    private val _moveToGodoMall = MutableLiveData<Event<String>>()
    val moveToGodoMall:LiveData<Event<String>>
        get() = _moveToGodoMall

    private val _moveToPingRecord = MutableLiveData<Event<Unit>>()
    val moveToPingRecord:LiveData<Event<Unit>>
        get() = _moveToPingRecord

    private val _moveToProduct = MutableLiveData<Event<String>>()
    val moveToProduct:LiveData<Event<String>>
        get() = _moveToProduct

    private val _signUpErrorPopup = MutableLiveData<Event<ErrorResponse>>()
    val signUpErrorPopup:LiveData<Event<ErrorResponse>>
        get() = _signUpErrorPopup

    private val _shopItemsErrorPopup = MutableLiveData<Event<ErrorResponse>>()
    val shopItemsErrorPopup:LiveData<Event<ErrorResponse>>
        get() = _shopItemsErrorPopup

    val loginIsVisible = MutableLiveData<Boolean>().apply { value = AppConstants.LOGIN_HEADER_IS_VISIBLE }
    val profileIsVisible = MutableLiveData<Boolean>().apply { value = AppConstants.PROFILE_HEADER_IS_VISIBLE }

    var godoUrl = ""
    var productUrl = ""

    fun loadData() = viewModelScope.launch {
        val response =
            shopRepository.getShopRecommendList(AppConstants.AUTH_KEY, AppConstants.ID)
        when (response) {
            is Resource.Success -> {
                _pingShopItems.value = response.value.data.recommendGoodsList
                _petName.value = "${response.value.data.pet.name}에게 "
                _pingAmount.value = response.value.data.availablePings.toString().toNumberFormat()
            }
            is Resource.Failure -> {
                response.errorBody?.let { body ->
                    val errorResponse = getErrorBodyConverter().convert(body)!!
                    _shopItemsErrorPopup.emit(errorResponse)
                }
            }
        }
    }

    fun goToMall() {
        shopSignUp()
    }

    fun goToRecord() {
        _moveToPingRecord.emit()
    }

    fun goToProduct(url: String) {
        this.productUrl = url
        shopSignUp()
    }

    fun scrollTop() {
        _scrollTopFlag.value = true
    }

    fun setTopBtnStatus(status:Boolean) {
        _topBtnVisible.value = status
    }

    fun shopSignUp() = viewModelScope.launch {
        val response = shopRepository.shopSignUp(AppConstants.AUTH_KEY, AppConstants.ID)
        when (response) {
            is Resource.Success -> {
                LogUtil.log("TAG", "smtUniqueId : ${response.value.data.smtUniqueId}")
                LogUtil.log("TAG", "shopEmpNo : ${response.value.data.shopEmpNo}")
                AppConstants.SHOP_UNIQUE_ID = response.value.data.smtUniqueId
                AppConstants.SHOP_EMP_NO = response.value.data.shopEmpNo

                godoUrl = response.value.data.gd5MainUrl
                if (productUrl.isEmpty()) {
                    _moveToGodoMall.emit(godoUrl)
                } else {
                    _moveToProduct.emit(productUrl)
                }

                productUrl = ""
            }
            is Resource.Failure -> {
                response.errorBody?.let { body ->
                    val errorResponse = getErrorBodyConverter().convert(body)!!
                    _signUpErrorPopup.emit(errorResponse)
                }
            }
        }
    }
}