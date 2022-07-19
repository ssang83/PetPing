package ai.comake.petping.data.vo

/**
 * android-petping-2
 * Class: Mission
 * Created by cliff on 2022/02/11.
 *
 * Description:
 */
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class OngoingMissionsResponse(
    val result: Boolean,
    val status: Number,
    val data: OngoingMissionData
)

data class OngoingMissionData(
    val profileId: Number,
    val pageNo: Number,
    val numOfRows: Number,
    val listSize: Number,
    val ongoingMissions: List<OngoingMission>
)

@Parcelize
data class OngoingMission(
    val id: Int?,
    val type: Int?,
    val typeStr: String?,
    val missionStateStr: String?,
    val missionState: Int?,
    val reward: String?,
    val targetName: String?,
    val startDate: String?,
    val endDate: String?,
    val name: String?,
    val missionDetailURL: String?,
    val missionCardDesign: MissionCardDesign?
) : Parcelable

@Parcelize
data class MissionCardDesign(
    val bgCode: String?,
    val imageURL: String?,
    val layoutType: Int?,
    val title: String?
) : Parcelable

data class CompletionMissionResponse(
    val result: Boolean,
    val status: Number,
    val data: CompletionMissionData
)

data class CompletionMissionData(
    val profileId: Number,
    val pageNo: Number,
    val numOfRows: Number,
    val listSize: Number,
    val completionMissions: List<CompletionMission>
)
@Parcelize
data class CompletionMission(
    val id: Number,
    val type: Int,
    val typeStr: String,
    val missionStateStr: String,
    val missionState: Int,
    val reward: String,
    val targetName: String,
    val name: String,
    val startDate: String,
    val endDate: String,
    val missionDetailURL: String,
    val missionCardDesign: MissionCardDesign?
): Parcelable