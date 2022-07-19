package ai.comake.petping.data.preference

/**
 * android-petping-2
 * Class: Preference
 * Created by cliff on 2022/06/28.
 *
 * Description:
 */
interface Preference {

    fun getAccessToken(): String
    fun setAccessToken(token: String)
    fun getUserName(): String
    fun setUserName(name: String)
    fun getUserId(): String
    fun setUserId(id: String)
    fun getUserEmail(): String
    fun setUserEmail(email: String)

    fun getSubscribeTopicList(): List<String>
    fun setSubscribeTopic(topic: String)
    fun unsetSubscribeTopic(topic: String)

    fun setDoNotShowPopup(id: Int)
    fun getDoNotShowPopupIdList(): List<String>

    fun setClosePopup(id: Int)
    fun getClosePopup(): List<String>
    fun removeAllClosePopup()
    fun removeAllNotShowPopup()

    fun setLastDate(date: String)
    fun getLastDate(): String

    fun isInitialLogin(): Boolean

    fun getUUID(): String
    fun setUUID(uuid: String)

    fun getAuthorityPopup(): Boolean
    fun setAuthorityPopup(clear: Boolean)

    fun getGuidePopupShow()
    fun setGuidePopupShow()

    fun logout()
}