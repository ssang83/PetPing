package ai.comake.petping.data.vo

/**
 * android-petping-2
 * Class: PetProfile
 * Created by cliff on 2022/02/24.
 *
 * Description:
 */
data class WalkHistoryData(
    val walks: List<PetWalk>,
    val pageNo: Int,
    val numOfRows: Int,
    val listSize: Int,
    val viewMode: String
)

data class PetWalk(
    val id: Int,
    val realWalkTime: String,
    val distance: Int,
    val review: String,
    val walkDate: String,
    val markingCount: Int,
    val walkImagesURL: List<String>,
    val pet: WalkHistoryPetInfo
)

data class WalkHistoryPetInfo(
    val id: Int,
    val name: String,
    val profileImageURL: String
)

data class WalkPathData(
    val petId: Int,
    val walkId: Int,
    val centerLat: Number,
    val centerLng: Number,
    val paths: List<HistoryPath>,
    val markings: List<HistoryMarking>
)

data class HistoryPath(
    val lat: Number,
    val lng: Number
)

data class HistoryMarking(
    val id: Int,
    val type: Int,
    val lat: Number,
    val lng: Number
)

data class WalkStatsData(
    val walkStats: List<WalkStats>,
    val barGraphData: BarGraphData
)

data class WalkStats(
    val startDate: String,
    val endDate: String,
    val walkDayCount: Int,
    val count: String,
    val distance: String,
    val time: String,
    val markingCount: String
)

data class BarGraphData(
    val values: List<Number>,
    val monthlyCount: List<MonthlyCount>
)

data class MonthlyCount(
    val month: Int,
    val count: Int
)

data class PetRnsData(
    val petRn: String,
    val gender: Int,
    val name: String,
    var breed: String
)

data class PetProfileData(
    val profileId: Int,
    val petId: Int,
    val petCharId: Int,
    val name: String,
    val gender: Int,
    val birth: String,
    val weight: Double,
    val breed: String,
    val charDefaultType: Int,
    val charDefaultColor: String,
    val charPatternType: Int,
    val charPatternColor: String,
    val charBodyType: Int,
    val charBodyColor: String,
    val recentWalkCountState: Int,
    val noWalkReason: String?,
    val petRn: String?,
    val profileImageURL: String,
    val profileType: Int,
    val memberInfoFamilyRegs: List<MemberInfoFamilyReg>,
    val myFamilyCode: String,
    val isPossibleWalk: Boolean,
    val isMissionPetSetting:Boolean,
    val isInsuranceAuth:Boolean,
    val isPublic:Boolean
)

data class MemberInfoFamilyReg(
    val memberId: String,
    val memberName: String,
    val email: String,
    val profileId: Int,
    val familyCode: String,
    val profileType: Int,
    val petId: Int
)

data class ModifyProfileResponseData(
    val profileId: Int,
    val petId: Int,
    val name: String,
    val gender: Int,
    val birth: String,
    val weight: Double,
    val breed: String,
    val isPossibleWalk: Boolean,
    val recentWalkCountState: Int,
    val noWalkReason: String,
    val petRn: String?,
    val profileImageURL: String
)

data class PetCharacterRequestBody(
    var petId: Int,
    var charDefaultType: Int,
    var charDefaultColor: String,
    var charPatternType: Int,
    var charPatternColor: String,
    var charBodyType: Int,
    var charBodyColor: String
)

data class ModifyProfileRequestData(
    var name: String,
    var gender: Int,
    var birth: String,
    var breed: String,
    var weight: Double,
    var isPossibleWalk: Boolean,
    var noWalkReason: String,
    var petRn: String,
    var isPublic: Boolean
)

data class PetCharacterResponseData(
    val petCharId: Int,
    val petId: Int,
    val charDefaultType: Int,
    val charDefaultColor: String,
    val charPatternType: Int,
    val charPatternColor: String,
    val charBodyType: Int,
    val charBodyColor: String
)

data class UnlinkBody(
    val petId: Int,
    val profileId: Int,
    val familyCode: String
)