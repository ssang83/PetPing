package ai.comake.petping.data.vo

/**
 * android-petping-2
 * Class: LocationHistoryResponse
 * Created by cliff on 2022/03/21.
 *
 * Description:
 */
data class LocationHistoryResponse(
    val listSize: Int,
    val numOfRows: Int,
    val personalLocationInformationInquiryLogs: List<PersonalLocationInformationInquiryLog>,
    val startIndex: Int
)

data class PersonalLocationInformationInquiryLog(
    val createDatetime: String,
    val id: Int,
    val inquiredMemberId: String,
    val inquiredMemberName: String,
    val memberId: String,
    val walkEndDatetime: String,
    val walkId: Int
)