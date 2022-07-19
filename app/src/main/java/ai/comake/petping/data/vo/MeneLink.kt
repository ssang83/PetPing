package ai.comake.petping.data.vo

import java.io.Serializable

sealed class MenuLink : Serializable {
    data class Fcm(
        val title: String,
        val message: String,
        val type: String
    ) : MenuLink()

    data class Airbridge(
        val type: String
    ) : MenuLink()

    data class PetPing(
        val type: String
    ) : MenuLink()
}

