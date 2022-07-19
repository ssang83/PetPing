package ai.comake.petping.data.vo

import android.os.Parcel
import android.os.Parcelable

/**
 * android-petping-2
 * Class: Info
 * Created by cliff on 2022/02/22.
 *
 * Description:
 */
data class MemberInfo(
    val personalInfoCollection: Boolean,
    val policyLocationService: Boolean,
    val birthAndGender: String?,
    val type: Int,
    val isEmailAuth: Boolean,
    val isIdentityAuth: Boolean,
    val flagSendAuthenticationEmail: Boolean,
    val typeStr: String,
    val phone: String,
    val policyService: Boolean,
    val name: String?,
    val pushMarketingInfo: Boolean,
    val over14yearsOld: Boolean,
    val id: String,
    val email: String?
)

data class AppInfo(
    val updateUrl_android: String,
    val androidNewAppVersion: String,
    val opensourceLicenseUrl: String,
    val policyLocationServiceUrl: String,
    val personalInfoCollectionUrl: String,
    val policyServiceUrl: String,
    val policyPetpingUrl: String,
    val pushMarketingInfo: String,
    val businessInformationUrl: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()?:"",
        parcel.readString()?:"",
        parcel.readString()?:"",
        parcel.readString()?:"",
        parcel.readString()?:"",
        parcel.readString()?:"",
        parcel.readString()?:"",
        parcel.readString()?:"",
        parcel.readString()?:""
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(updateUrl_android)
        parcel.writeString(androidNewAppVersion)
        parcel.writeString(opensourceLicenseUrl)
        parcel.writeString(policyLocationServiceUrl)
        parcel.writeString(personalInfoCollectionUrl)
        parcel.writeString(policyServiceUrl)
        parcel.writeString(policyPetpingUrl)
        parcel.writeString(pushMarketingInfo)
        parcel.writeString(businessInformationUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AppInfo> {
        override fun createFromParcel(parcel: Parcel): AppInfo {
            return AppInfo(parcel)
        }

        override fun newArray(size: Int): Array<AppInfo?> {
            return arrayOfNulls(size)
        }
    }
}