package ai.comake.petping.data.vo

import ai.comake.petping.BR
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable

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
    val data: PetProfile
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
    val profileImageURL: String,
    val isMissionPet:Boolean
)

data class InteractionStatus(
    private var inputStatus: Boolean = false,
    private var validation: Boolean = true,
    private var focusHintVisible: Boolean = true,
    private var lineStatus: Boolean = false,
    private var helperVisible: Boolean = false
) : BaseObservable() {

    @Bindable
    fun getInputStatus() = inputStatus

    fun setInputStatus(inputStatus: Boolean) {
        this.inputStatus = inputStatus
        notifyPropertyChanged(BR.inputStatus)
    }

    @Bindable
    fun getValidation() = validation

    fun setValidation(validation: Boolean) {
        this.validation = validation
        notifyPropertyChanged(BR.validation)
    }

    @Bindable
    fun getFocusHintVisible() = focusHintVisible

    fun setFocusHintVisible(focusHintVisible: Boolean) {
        this.focusHintVisible = focusHintVisible
        notifyPropertyChanged(BR.focusHintVisible)
    }

    @Bindable
    fun getLineStatus() = lineStatus

    fun setLineStatus(lineStatus: Boolean) {
        this.lineStatus = lineStatus
        notifyPropertyChanged(BR.lineStatus)
    }

    @Bindable
    fun getHelperVisible() = helperVisible

    fun setHelperVisible(helperVisible: Boolean) {
        this.helperVisible = helperVisible
        notifyPropertyChanged(BR.helperVisible)
    }
}