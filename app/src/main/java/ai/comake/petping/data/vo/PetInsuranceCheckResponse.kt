package ai.comake.petping.data.vo

/**
 * android-petping-2
 * Class: PetInsuranceCheckResponse
 * Created by cliff on 2022/03/22.
 *
 * Description:
 */
data class PetInsuranceCheckResponse(
    val `data`: UrlData,
    val result: Boolean,
    val status: Int
)

data class UrlData(
    val insurJoinURL: String
)