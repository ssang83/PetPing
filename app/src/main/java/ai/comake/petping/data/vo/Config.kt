package ai.comake.petping.data.vo

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * android-petping-2
 * Class: WebConfig
 * Created by cliff on 2022/02/16.
 *
 * Description:
 */
@Parcelize
data class WebConfig(
    val url:String,
    val fromWelcomKit:Boolean = false,
    val rewardCashBack:Boolean = false,
    val insurance:Boolean = false,
    val fromHome:Boolean = false,
    val welcomeDetail:Boolean = false
) : Parcelable

@Parcelize
data class PetProfileConfig(
    val petId:Int,
    val viewMode:String
) : Parcelable

@Parcelize
data class AgreementConfig(
    val snsToken:String = "",
    val authWord:String,
    val signUpType:Int,
    val emailKey:String,
    val nickName:String = "",
    val sendAuthMailFlag:Boolean
) : Parcelable

@Parcelize
data class ChangePhoneNumberConfig(
    val name:String,
    val birthAndGender:String
) : Parcelable

@Parcelize
data class CertWebConfig(
    val name:String,
    val birth:String,
    val gender:String
) : Parcelable

@Parcelize
data class CIConfig(
    val ci:String,
    val phoneNumber:String
) : Parcelable

@Parcelize
data class PetCharacterConfig(
    var petId: Int,
    var petCharId: Int,
    var charDefaultType: Int,
    var charDefaultColor: String,
    var charPatternType: Int,
    var charPatternColor: String,
    var charBodyType: Int,
    var charBodyColor: String
) : Parcelable

@Parcelize
data class FamilyConfrimConfig(
    var familyCode: String,
    var profile: FamilyProfile
) : Parcelable

@Parcelize
data class AppleLoginConfig(
    var email: String,
    var authWord: String,
    var authCode: String
) : Parcelable