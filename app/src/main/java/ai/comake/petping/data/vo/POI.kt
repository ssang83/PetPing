package ai.comake.petping.data.vo

import android.os.Parcelable

data class MarkingPoi(
    val clusteredCount: Int,
    val clusteredId: Int,
    val lat: String,
    val lng: String,
    val pois: List<Pois>
) {
    data class Pois(
        val id: Int,
        val petId: Int,
        val petName: String,
        val profileImageURL: String,
        val recentVisit: String
    )
}

data class PlacePoi(
    val places: List<Places>
) {
    data class Places(
        val id: Int,
        val type: Int,
        val lat: String,
        val lng: String
    )
}

data class MarkingDetail(
    val marking: Marking?,
    val pet: Pet?
) {
    data class Pet(
        val age: String,
        val breed: String,
        val gender: String,
        val id: Int,
        val name: String,
        val profileImageURL: String
    ) {
        val getPetName get() = name
        val getPetDesc
            get() = "$breed . $age . $gender"
    }

    data class Marking(
        val recentVisitTime: String?,
        val visitCount: Int?
    ) {
        val getRecentVisitTime get() = "${recentVisitTime} 다녀갔어요"
        val getRecentVisitCount get() = "총 ${visitCount}회 방문"
    }
}

data class PlaceDetail(
    val place: Place?
) {
    data class Place(
        val id: String,
        val name: String,
        val poiDesc0: String,
        val poiDesc1: String,
        val poiDesc2: String,
        val poiDesc3: String,
        val placeImagesURL: List<String>
    )
}