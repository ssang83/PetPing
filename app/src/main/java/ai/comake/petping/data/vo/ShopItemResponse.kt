package ai.comake.petping.data.vo

data class ShopItemResponse(
    val pet: PetInfo,
    val detailPings: ShopDetailPings,
    val popupList: List<ShopPopup>,
    val recommendGoodsList: List<RecommendGoods>
)

data class PetInfo(
    val id: Int,
    val name: String?,
    val gender:Int,
    val breed:String,
    val birthday:String,
    val weight:Double,
    val isPossibleWalk:Boolean,
    val rn:String,
    val isNeuter:Boolean
)

data class RecommendGoods(
    val content: String,
    val discountRate: Int,
    val finalAmount: Int,
    val id: Int,
    val imageURL: String,
    val linkURL: String,
    val price: Int,
    val subTitle: Any,
    val title: String
)

data class ShopDetailPings(
    val totalSave: Int,
    val totalUse: Int,
    val expireSoon: Int,
    val expire: Int,
    val availablePings: Int
)

data class ShopPopup(
    val contentURL: String,
    val bgCode: String,
    val imageURL: String,
    val id: Number,
    val type: Number, // 1 : 홈, 2 : 더보기, 3 : 펫핑샵
    val startDatetime: String,
    val endDatetime: String,
    val description: String
)

