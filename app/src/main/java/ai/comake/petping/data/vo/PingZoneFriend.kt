package ai.comake.petping.data.vo

/**
 * android-petping-2
 * Class: PingZoneFriend
 * Created by cliff on 2022/06/20.
 *
 * Description:
 */
data class PingZoneResponse(
    val result: Boolean,
    val status: Number,
    val data: PingZoneData
)

data class PingZoneData(
    val pingzoneMeetPets: List<PingZoneMeetPet>
)

data class PingZoneMeetPet(
    val id: Int,
    val name: String,
    val profileImageURL: String,
    val meetDatetime: String,
    val gender: String,
    val breed: String
)