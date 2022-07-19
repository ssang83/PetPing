package ai.comake.petping.data.vo

data class ShopItemResponse(
    val pet: PetInfo,
    val availablePings:Int,
    val recommendGoodsList: List<RecommendGoods>
)

data class PetInfo(
    val id: Int,
    val name: String
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