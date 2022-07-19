package ai.comake.petping.data.vo

/**
 * android-petping-2
 * Class: Profile
 * Created by cliff on 2022/06/10.
 *
 * Description:
 */
data class ProfileRequest(
    val memberId: String,
    val name: String,
    val gender: Number,
    val birth: String,
    val breed: String,
    val weight: Double,
    val charDefaultType: String,
    val charDefaultColor: String,
    val charPatternType: String,
    val charPatternColor: String,
    val charBodyType: String,
    val charBodyColor: String,
    val recentWalkCountState: Number,
    val noWalkReason: String,
    val petRn: String
)

data class ProfileResponse(
    val result:Boolean,
    val status:Number,
    val data: ProfileData
)

data class ProfileData(
    val pet: PetProfile
)

data class PetProfile(
    val petId: Number,
    val name:String,
    val gender:Number,
    val birth: String,
    val weight: Double,
    val breed: String,
    val charDefaultType: Number,
    val charDefaultColor: Number,
    val charPatternType: Number,
    val charPatternColor: Number,
    val charBodyType: Number,
    val charBodyColor: Number,
    val recentWalkCountState: Number,
    val noWalkReason: String,
    val profileImageURL: String
)