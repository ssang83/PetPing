package ai.comake.petping.data.vo

/**
 * android-petping-2
 * Class: DashboardResponse
 * Created by cliff on 2022/05/19.
 *
 * Description:
 */
data class DashboardResponse(
    val result: Boolean,
    val status: Number,
    val data: DashboardData
)

data class DashboardData(
    val popup: List<Popup>,
    val petMotionState: String,
    val petMessage: String,
    val weather: Weather,
    val pet: DashboardPet,
    val walk: WalkInfo,
    val rewardMissionAlert: RewardMission?,
    val speechBubble: SpeechBubble?,
    val eventWelcomeKitAlert: EventWelcomeKit?,
    val missionPetSettingAlert: MissionPetSettingAlert?
)

data class SpeechBubble(
    val id: Int,
    val text: String,
    val linkURL: String
)

data class Popup(
    val id: Int,
    val popupImageURL: String,
    val linkURL: String
)

data class Weather(
    val skyState: Number?,
    val value: String?,
    val today: WeatherInfo,
    val tomorrow: WeatherInfo
)

data class WeatherInfo(
    val skyState: Number,
    val walkIndex: String
)

data class DashboardPet(
    val id: Int,
    val name: String,
    val defaultColor: String,
    val defaultType: Number,
    val patternColor: String,
    val patternType: Number,
    val bodyColor: String,
    val bodyType: Number,
    val profileImageURL: String,
    val isPossibleWalk: Boolean
)

data class WalkInfo(
    val dayCount: String,
    val totalTime: String,
    val totalDistance: String,
    val statsType: String
)

data class RewardMission(
    val id: Int,
    val alert: RewardMissionAlert,
    val detailAlert: RewardMissionDetail
)

data class RewardMissionAlert(
    val title: String
)

data class RewardMissionDetail(
    val title: String,
    val content: String
)

data class EventWelcomeKit(
    val alert: EventWelcomeKitAlert,
    val detailAlert: EventWelcomeKitDetail
)

data class EventWelcomeKitAlert(
    val title:String
)

data class EventWelcomeKitDetail(
    val applyUrl:String,
    val detailUrl:String,
    val title: String,
    val content: String
)

data class MissionPetSettingAlert(
    val alert: MissionPetAlert,
    val detailAlert: MissionPetAlertDetail
)

data class MissionPetAlert(
    val title:String
)

data class MissionPetAlertDetail(
    val title: String,
    val content: String
)

data class BadgeResponse(
    val data: BadgeData?,
    val result: Boolean,
    val status: Int
)

data class BadgeData(
    val androidNewAppVersion: String?,
    val iosNewAppVersion: String?,
    val newMissionId: Int?,
    val newNoticeId: Int?,
    val newReplyId: Int?,
    val newSaveRewardId: Int?
)

data class DashboardAnimationInfo(
    val skyState:Number,
    val dashboardData: DashboardData
)

data class WalkHistory(
    val id:Int,
    val viewMode:String
)