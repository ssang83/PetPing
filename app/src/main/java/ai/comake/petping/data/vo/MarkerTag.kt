package ai.comake.petping.data.vo

import ai.comake.petping.ui.home.walk.WalkBottomUi
import android.os.Parcelable

data class MarkerTag(
    val id: Int,
    val type: WalkBottomUi,
    val count: Int = 1,
    val placePoi: PlacePoi? = null,
)