package ai.comake.petping.data.vo

/**
 * android-petping-2
 * Class: WalkablePet
 * Created by cliff on 2022/02/11.
 *
 * Description:
 */
data class Breed(
    val breeds: List<String>
)

data class PetsData(
    val pets: List<Pet>,
    val memberId: String
)

data class Pet(
    val profileId: Number,
    val id: Number,
    val name: String,
    val breed: String,
    val age: String,
    val gender: String,
    val profileImageURL: String,
    val isFamilyProfile: Boolean
)