package ai.comake.petping

import ai.comake.petping.util.getBitmapFromInputStream
import android.content.Context
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException

fun makeShopLoginBody(): RequestBody {
    val accessToken = AppConstants.AUTH_KEY.split(" ")[1]
    return JSONObject().apply {
        put("shopEmpNo", AppConstants.SHOP_EMP_NO)
        put("smtUniqueId", AppConstants.SHOP_UNIQUE_ID)
        put("smtAccessToken", accessToken)
    }.toString().toRequestBody("application/json".toMediaTypeOrNull())
}

fun Context.makeMissionFileUploadBody(fileList:List<String>): List<MultipartBody.Part> {
    val body = mutableListOf<MultipartBody.Part>()
    fileList.forEachIndexed { index, s ->
        var contents = try {
            contentResolver.openInputStream(Uri.parse(s))
        } catch (e: FileNotFoundException) {
            FileInputStream(File(s))
        }

        val outputStream = getBitmapFromInputStream(contents!!, this, s)
        val filePart = MultipartBody.Part.createFormData(
            "file", "${index}.png", outputStream.toByteArray().toRequestBody(
                "image/png".toMediaTypeOrNull(), 0,
                outputStream.size()
            )
        )

        body.add(filePart)
    }

    return body
}

fun makeMissionPetBody(petId:Int): RequestBody {
    return JSONObject().apply {
        put("petId", petId)
        put("memberId", AppConstants.ID)
    }.toString().toRequestBody("application/json".toMediaTypeOrNull())
}

fun makeAuthMailBody(email:String): RequestBody {
    return JSONObject().apply {
        put("memberId", AppConstants.ID)
        put("email", email)
    }.toString().toRequestBody("application/json".toMediaTypeOrNull())
}

/**
 * 펫 동물등록번호 유효성 검사 body
 *
 * @param rn
 * @return
 */
fun makePetRNSModifyBody(
    rn: String,
    name: String
): RequestBody {
    return JSONObject().apply {
        put("rn", rn)
        put("ownerNm", name)
    }.toString().toRequestBody("application/json".toMediaTypeOrNull())
}

/**
 * 푸쉬 토큰 등록 body
 *
 * @param memberId
 * @param fcmToken
 * @return
 */
fun makeFCMBody(
    memberId: String,
    fcmToken: String
): RequestBody {
    return JSONObject().apply {
        put("memberId", memberId)
        put("fcmToken", fcmToken)
    }.toString().toRequestBody("application/json".toMediaTypeOrNull())
}

/**
 * 가족 등록 해제한다.
 *
 * @param petId
 * @param profileId
 * @param familyCode
 * @return
 */
fun makeUnlinkFamilyBody(
    petId: Int,
    profileId: Int,
    familyCode: String
): RequestBody {
    return JSONObject().apply {
        put("petId", petId)
        put("profileId", profileId)
        put("familyCode", familyCode)
    }.toString().toRequestBody("application/json".toMediaTypeOrNull())
}

fun makePushMarketingInfoBody(status:Boolean): RequestBody {
    return JSONObject().apply {
        put("pushMarketingInfo", status)
    }.toString().toRequestBody("application/json".toMediaTypeOrNull())
}

fun makeNotificationStatusBody(status:Boolean): RequestBody {
    return JSONObject().apply {
        put("memberId", AppConstants.ID)
        put("value", status)
    }.toString().toRequestBody("application/json".toMediaTypeOrNull())
}

fun makeChangeMemberInfoBody(
    email:String = "",
    password:String = "",
    phone:String = "",
    pushMarketingInfo:String = ""
): RequestBody {
    return JSONObject().apply {
        put("password", password)
        put("phone", phone)
        put("email", email)
        put("pushMarketingInfo", pushMarketingInfo)
    }.toString().toRequestBody("application/json".toMediaTypeOrNull())
}

fun makeLoginBody(
    type: String,
    email: String,
    authWord: String,
    deviceId: String,
    token: String
): RequestBody {
    return JSONObject().apply {
        put("type", type)
        put("email", email)
        put("authWord", authWord)
        put("deviceId", deviceId)
        put("snsAuthToken", token)
    }.toString().toRequestBody("application/json".toMediaTypeOrNull())
}

fun makeSignUpBody(
    email: String,
    type: Int,
    authWord: String,
    deviceId: String,
    snsAuthToken: String,
    policyService: Boolean,
    policyLocationService: Boolean,
    personalInfoCollection: Boolean,
    pushMarketingInfo: Boolean,
    over14YearsOld: Boolean
): RequestBody {
    return JSONObject().apply {
        put("type", type)
        put("email", email)
        put("authWord", authWord)
        put("deviceId", deviceId)
        put("snsAuthToken", snsAuthToken)
        put("policyService", policyService)
        put("policyLocationService", policyLocationService)
        put("personalInfoCollection", personalInfoCollection)
        put("pushMarketingInfo", pushMarketingInfo)
        put("over14YearsOld", over14YearsOld)
    }.toString().toRequestBody("application/json".toMediaTypeOrNull())
}

fun makeFindPasswordBody(email: String): RequestBody {
    return JSONObject().apply {
        put("email", email)
    }.toString().toRequestBody("application/json".toMediaTypeOrNull())
}

fun makePhoneAuthBody(
    phone: String,
    ci: String,
    name: String,
    birthAndGender: String
): RequestBody {
    return JSONObject().apply {
        put("phone", phone)
        put("ci", ci)
        put("name", name)
        put("birthAndGender", birthAndGender)
    }.toString().toRequestBody("application/json".toMediaTypeOrNull())
}

fun makeWithdrawalBody(
    leaveType: Int,
    leaveOtherReason: String
): RequestBody {
    return JSONObject().apply {
        put("leaveType", leaveType)
        put("leaveOtherReason", leaveOtherReason)
    }.toString().toRequestBody("application/json".toMediaTypeOrNull())
}

fun makeInsuranceConnectBody(
    petId: Int,
    insuredRrn: String,
    insuredName: String
): RequestBody {
    return JSONObject().apply {
        put("petId", petId)
        put("insuredRrn", insuredRrn)
        put("insuredName", insuredName)
        put("memberId", AppConstants.ID)
    }.toString().toRequestBody("application/json".toMediaTypeOrNull())
}

fun makePetCharacterBody(
    petId: Int,
    charDefaultType: Int,
    charDefaultColor: String,
    charPatternType: Int,
    charPatternColor: String,
    charBodyType: Int,
    charBodyColor: String
): RequestBody {
    return JSONObject().apply {
        put("petId", petId)
        put("charDefaultType", charDefaultType)
        put("charDefaultColor", charDefaultColor)
        put("charPatternType", charPatternType)
        put("charPatternColor", charPatternColor)
        put("charBodyType", charBodyType)
        put("charBodyColor", charBodyColor)
    }.toString().toRequestBody("application/json".toMediaTypeOrNull())
}

fun makePetProfileModifyBody(
    name: String,
    gender: String,
    birth: String,
    breed: String,
    weight: String,
    isPossibleWalk: String,
    noWalkReason: String,
    petRn: String,
    isPublic: String
) : HashMap<String, RequestBody> {
    val body = HashMap<String, RequestBody>()
    body["name"] = name.toRequestBody("multipart/form-data".toMediaTypeOrNull())
    body["gender"] = gender.toRequestBody("multipart/form-data".toMediaTypeOrNull())
    body["birth"] = birth.toRequestBody("multipart/form-data".toMediaTypeOrNull())
    body["breed"] = breed.toRequestBody("multipart/form-data".toMediaTypeOrNull())
    body["weight"] = weight.toRequestBody("multipart/form-data".toMediaTypeOrNull())
    body["petRn"] = petRn.toRequestBody("multipart/form-data".toMediaTypeOrNull())
    body["isPossibleWalk"] = isPossibleWalk.toRequestBody("multipart/form-data".toMediaTypeOrNull())
    body["noWalkReason"] = noWalkReason.toRequestBody("multipart/form-data".toMediaTypeOrNull())
    body["isPublic"] = isPublic.toRequestBody("multipart/form-data".toMediaTypeOrNull())

    return body
}

fun makeRegisterFamilyProfileBody(
    petId: Int,
    familyCode: String
): RequestBody {
    return JSONObject().apply {
        put("petId", petId)
        put("familyCode", familyCode)
        put("memberId", AppConstants.ID)
    }.toString().toRequestBody("application/json".toMediaTypeOrNull())
}

fun makeNewProfileBody(
    memberId: String,
    name: String,
    gender: String,
    birth: String,
    breed: String,
    weight: String,
    charDefaultType: String,
    charDefaultColor: String,
    charPatternType: String,
    charPatternColor: String,
    charBodyType: String,
    charBodyColor: String,
    recentWalkCountState: Number,
    noWalkReason: String,
    petRn: String
) : HashMap<String, RequestBody> {
    val body = HashMap<String, RequestBody>()
    body["memberId"] = memberId.toRequestBody("multipart/form-data".toMediaTypeOrNull())
    body["name"] = name.toRequestBody("multipart/form-data".toMediaTypeOrNull())
    body["gender"] = gender.toRequestBody("multipart/form-data".toMediaTypeOrNull())
    body["birth"] = birth.toRequestBody("multipart/form-data".toMediaTypeOrNull())
    body["breed"] = breed.toRequestBody("multipart/form-data".toMediaTypeOrNull())
    body["weight"] = weight.toRequestBody("multipart/form-data".toMediaTypeOrNull())
    body["charDefaultType"] = charDefaultType.toRequestBody("multipart/form-data".toMediaTypeOrNull())
    body["charDefaultColor"] = charDefaultColor.toRequestBody("multipart/form-data".toMediaTypeOrNull())
    body["charPatternType"] = charPatternType.toRequestBody("multipart/form-data".toMediaTypeOrNull())
    body["charPatternColor"] = charPatternColor.toRequestBody("multipart/form-data".toMediaTypeOrNull())
    body["charBodyType"] = charBodyType.toRequestBody("multipart/form-data".toMediaTypeOrNull())
    body["charBodyColor"] = charBodyColor.toRequestBody("multipart/form-data".toMediaTypeOrNull())
    body["recentWalkCountState"] = recentWalkCountState.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
    body["petRn"] = petRn.toRequestBody("multipart/form-data".toMediaTypeOrNull())
    body["noWalkReason"] = noWalkReason.toRequestBody("multipart/form-data".toMediaTypeOrNull())

    return body
}