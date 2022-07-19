package ai.comake.petping.data.vo

/**
 * android-petping-2
 * Class: MyPage
 * Created by cliff on 2022/02/16.
 *
 * Description:
 */
data class InsurancePet(
    val petId: Int,
    val petName: String,
    val profileImageURL: String
)

data class PetInsuranceMissionPet(
    val insuEndDate: String,
    val insuProductStr: String,
    val insuStartDate: String,
    val petId: Int,
    val petName: String,
    val profileImageURL: String
)

data class RepresentativePet(
    val petId: Number,
    val petName: String,
    val profileImageURL: String,
    val settingDate: String,
    val applicationDate: String,
    val isChangeable: Boolean
)

data class InquiryData(
    val id: Int,
    val title: String,
    val categoryStr:String,
    val updateDateTime: String?,
    val createDateTime: String,
    val url: String,
    val isAnswered: Boolean
)

data class LeaveType(
    val leaveType: Int,
    val leaveTypeStr: String
)

data class PetInsurJoinsData(
    val insuCompany: String,
    val insuProductStr: String,
    val url: String,
    val state:Int,
    val insuStartDate:String,
    val insuEndDate: String
)

data class InquiryType(
    val inquiryType: Int,
    val inquiryTypeStr: String
)