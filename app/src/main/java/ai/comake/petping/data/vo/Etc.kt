package ai.comake.petping.data.vo

/**
 * android-petping-2
 * Class: Etc
 * Created by cliff on 2022/02/18.
 *
 * Description:
 */
data class NoticeResponseData(
    val id: Number,
    val title: String,
    val updateDateTime: String,
    val url: String,
    val createDateTime: String,
    val isFixingPin: Boolean
)

data class MyPageData(
    val myInfos : MyInfo,
    val popupInfos: List<PopupInfo>,
    val myPets: List<MyPet>,
    val snsChannels: SNSChannel,
    val welcomeKit: WelcomeKit,
    val claimURL:String
)

data class MyInfo (
    val name: String?,
    val isEmailAuth: Boolean,
    val email: String?,
    val isFirstMissionCompleted:Boolean
)

data class PopupInfo (
    val contentURL: String,
    val bgCode: String,
    val imageURL: String
)

data class MyPet(
    val isFamilyProfile: Boolean,
    val isAddInfo: Boolean,
    val name: String,
    val id: Int,
    val profileImageURL: String
)

data class SNSChannel(
    val twitter: String,
    val facebook: String,
    val instagram: String
)

data class WelcomeKit(
    val url:String
)