package ai.comake.petping.data.vo

/**
 * android-petping-2
 * Class: ContentsResponse
 * Created by cliff on 2022/05/19.
 *
 * Description:
 */
data class ContentsResponse(
    val result: Boolean,
    val status: Number,
    val data: Content
)

data class Content(
    val boardBannerList: List<Tip>
)

data class Tip(
    val id: Number,
    val thumbnailURL: String,
    val mainText: String,
    val webViewURL: String
)