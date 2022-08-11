package ai.comake.petping.ui.home.shop

import ai.comake.petping.AppConstants
import ai.comake.petping.Event
import ai.comake.petping.api.Resource
import ai.comake.petping.data.repository.ShopRepository
import ai.comake.petping.data.vo.ErrorResponse
import ai.comake.petping.data.vo.RecommendGoods
import ai.comake.petping.data.vo.ShopPopup
import ai.comake.petping.emit
import ai.comake.petping.util.LogUtil
import ai.comake.petping.util.getErrorBodyConverter
import ai.comake.petping.util.toNumberFormat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.appbar.AppBarLayout
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

    private val _moveToBannerDetail = MutableLiveData<Event<String>>()
    val moveToBannerDetail:LiveData<Event<String>>
        get() = _moveToBannerDetail

    private val _isShowButton = MutableLiveData<Boolean>().apply { value = false }
    val isShowButton:LiveData<Boolean> get() = _isShowButton

    private val _isShowBanner = MutableLiveData<Boolean>().apply { value = false }
    val isShowBanner:LiveData<Boolean> get() = _isShowBanner

    private val _bannerUpdate = MutableLiveData<Event<Unit>>()
    val bannerUpdate:LiveData<Event<Unit>> get() = _bannerUpdate

    private val _bannerList = MutableLiveData<List<ShopPopup>>()
    val bannerList:LiveData<List<ShopPopup>> get() = _bannerList

    val ballonStatus = MutableLiveData<Boolean>().apply { value = false }

    var godoUrl = ""
    var productUrl = ""
    var bannerItems = listOf<ShopPopup>()

    val appBarScrollListener = object : AppBarLayout.OnOffsetChangedListener {
        override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
            closeBallon()
            val maxScrollSize = appBarLayout.totalScrollRange
            val currentScrollPercentage = (Math.abs(verticalOffset)) * 100 / maxScrollSize

            if (currentScrollPercentage >= 50) {
                _isShowButton.value = true
            }

            if (currentScrollPercentage < 50) {
                _isShowButton.value = false
            }
        }
    }

    fun loadData() = viewModelScope.launch {
        val response =
            shopRepository.getShopRecommendList(AppConstants.AUTH_KEY, AppConstants.ID)
        when (response) {
            is Resource.Success -> {
                if (response.value.status == "200") {
                    _pingShopItems.value = response.value.data.recommendGoodsList
                    _petName.value = if(response.value.data.pet.name == null) {
                        "펫핑이 "
                    } else {
                        "${response.value.data.pet.name}에게 "
                    }
                    _pingAmount.value = response.value.data.detailPings.availablePings.toString().toNumberFormat()
                    if (response.value.data.popupList.size > 0) {
                        _bannerList.value = response.value.data.popupList
                        bannerItems = response.value.data.popupList
                        _bannerUpdate.emit()
                        _isShowBanner.value = true
                    } else {
                        _isShowBanner.value = false
                    }
                } else {
                    _shopItemsErrorPopup.emit(response.value.error)
                }
            }
            is Resource.Error -> {
                response.errorBody?.let { body ->
                    _shopItemsErrorPopup.emit(body)
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

    fun showBallon() {
        ballonStatus.value = true
    }

    fun closeBallon() {
        ballonStatus.value = false
    }

    fun onBannerClicked(url: String) {
        _moveToBannerDetail.emit(url)
    }
}