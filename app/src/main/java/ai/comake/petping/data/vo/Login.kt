package ai.comake.petping.data.vo

import android.os.Parcel
import android.os.Parcelable

/**
 * android-petping-2
 * Class: Login
 * Created by cliff on 2022/02/23.
 *
 * Description:
 */
data class DuplicationResponse(
    val result: Boolean,
    val status: Number,
    val code: String,
    val message: String,
    var errorResponse: ErrorResponse
)

data class PolicyData(
    val id: Int,
    val type: Int,
    val policyURL: String
)

data class SignUpResponseData(
    val authorizationToken: String,
    val tokenExpireAt: String,
    val id: String,
    val email: String,
    val name: String,
    val isEmailAuthSend: Boolean
)

data class SignInResponseData(
    val authorizationToken: String,
    val name: String,
    val id: String,
    val email: String,
    val isEmailAuthSend:Boolean
)

data class FamilyProfile(
    val profileImageURL: String,
    val petId: Int,
    val name: String,
    val breed: String,
    val birth: String,
    val age: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()?:"",
        parcel.readInt(),
        parcel.readString()?:"",
        parcel.readString()?:"",
        parcel.readString()?:"",
        parcel.readString()?:""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(profileImageURL)
        parcel.writeInt(petId)
        parcel.writeString(name)
        parcel.writeString(breed)
        parcel.writeString(birth)
        parcel.writeString(age)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FamilyProfile> {
        override fun createFromParcel(parcel: Parcel): FamilyProfile {
            return FamilyProfile(parcel)
        }

        override fun newArray(size: Int): Array<FamilyProfile?> {
            return arrayOfNulls(size)
        }
    }
}

data class NaverResponse(
    val resultCode: String,
    val message: String,
    var response: NaverData
)

data class NaverData(
    val id: String?,
    val nickname: String?,
    val profile_image: String?,
    val email: String?
)

data class FCMResponse(
    val memberId: String,
    val fcmToken: String
)

data class FCMNotification(
    val event: Boolean,
    val reward: Boolean,
    val walk: Boolean,
    val etc: Boolean
)