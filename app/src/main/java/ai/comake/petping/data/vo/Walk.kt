package ai.comake.petping.data.vo

import ai.comake.petping.util.toHHMMSSFormat
import android.location.Location
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import java.io.File

data class WalkablePet(
    val pets: List<Pets>,
    val memberId: String
) {
    data class Pets(
        val isFamilyProfile: Boolean,
        val profileImageURL: String,
        val allTogetherImageURL: Int?,
        val name: String,
        val id: Int
    )
}

data class WalkPath(
    val location: Location,
    val state: Int,
    var lat: String,
    var lng: String
)

data class WalkStartRequest(
    var memberId: String,
    var petIds: List<Int>,
    var lat: String,
    var lng: String
)

data class WalkStart(
    val walk: Walk
) {
    data class Walk(
        val petIds: List<Int>,
        val id: Number,
        val memberId: String
    )
}

data class WalkFinishRequest(
    val distance: Double,
    var realWalkTime: String,
    val endState: Int,
    val walkPaths: List<WalkPath>,
    val walkEndDatetimeMilli: Long
) {
    data class WalkPath(
        val location: Location,
        var state: Int,
        var lat: String,
        var lng: String
    )
}

data class WalkFinish(
    var walk: Walk,
    var isMissionAchievement: Boolean,
    var missionReward: Int,
    var pets: List<Pets>
) {
    data class Walk(
        var id: Int,
        var distance: Double,
        var realWalkTime: List<Int>,
        var markingCount: Int,
        var distanceString: String,
        var endState: Int
    )

    data class Pets(
        var id: Int,
        var name: String,
        var markingCount: Int
    )
}

data class WalkRecordingResponseData(
    val walks: List<WalkRecord>,
    val startIndex: Int,
    val numOfRows: Int,
    val listSize: Int,
    val viewMode: String,
    val isDeleteRight: Boolean,
    val isPublicWalkingRecords: Boolean
)

data class WalkRecord(
    val id: Int,
    val realWalkTime: String,
    val distance: String,
    val review: String,
    val walkDate: String,
    val markingCount: Int,
    val walkImagesURL: List<String>,
    val pet: RecordPet,
    val memberName: String,
    val walkStartDatetime: String,
    val walkEndDatetime: String,
    val isMemberName: Boolean,
    val withPets: List<String>
)

data class RecordPet(
    val id: Int,
    val name: String,
    val profileImageURL: String
)

data class WalkAudioGuide(
    val pageNo: Int = 0,
    val listSize: Int = 0,
    val audioGuideList: ArrayList<AudioGuideItem>?,
    val numOfRows: Int = 0
)

data class AudioGuideItem(
    var audioFileUrl: String = "",
    var speakerThumbnailFileSeq: Int = 0,
    var listThumbnailFileSeq: Int = 0,
    var description: String = "",
    var runningTime: Int =  0,
    var title: String = "",
    var createDatetime: Long = 0,
    var tagList: List<String> = emptyList(),
    var speakerThumbnailFileUrl: String = "",
    var audioFileId: Int = 0,
    var listThumbnailFileUrl: String = "",
    var id: Int = 0,
    var tag: String = "",
    var hasAudio: Boolean = false,
    var percent: String? = "",
    var progress: Int? = 0,
    var readyProgress: Boolean = false
) {
    val getRunningTime get() = "소요시간 · ${runningTime.toHHMMSSFormat()}"
}

data class AudioGuideStatus(
    var isPlaying: Boolean = false,
    var totalTime: String = "00:00",
    var progressTime: String = "00:00",
    var titleName: String = "",
    var speakerImageUrl: String = "",
    var id: Int = 0,
    var audioFileId: Int = 0
) {

}

data class DownLoadProgress(
    val percent: Int = 0,
    val position: Int = 0
) {
}

sealed class Download {
    data class Progress(val percent: Int) : Download()
    data class Finished(val file: File) : Download()
}