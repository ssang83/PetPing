package ai.comake.petping.util

import ai.comake.petping.AppConstants
import ai.comake.petping.AppConstants.PREF_KEY_AUTHORITY_POP_UP
import ai.comake.petping.AppConstants.PREF_KEY_CLOSE
import ai.comake.petping.AppConstants.PREF_KEY_CURRENT_USER_EMAIL
import ai.comake.petping.AppConstants.PREF_KEY_CURRENT_USER_NAME
import ai.comake.petping.AppConstants.PREF_KEY_DO_NOT_SHOW
import ai.comake.petping.AppConstants.PREF_KEY_GUIDE_POP_UP_SHOW
import ai.comake.petping.AppConstants.PREF_KEY_INITIAL
import ai.comake.petping.AppConstants.PREF_KEY_LAST_DATE
import ai.comake.petping.AppConstants.PREF_KEY_TOPIC_LIST
import ai.comake.petping.AppConstants.PREF_KEY_UUID_KEY
import ai.comake.petping.data.vo.UserDataStore
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import javax.inject.Inject

class SharedPreferencesManager @Inject constructor(private val sharedPreferences: SharedPreferences) {

    fun saveLoginDataStore(userDataStore: UserDataStore) {
        sharedPreferences.edit()
            .putString(AppConstants.PREF_KEY_ACCESS_TOKEN, userDataStore.access_token).apply()
        sharedPreferences.edit().putString(AppConstants.PREF_KEY_USER_ID, userDataStore.user_id)
            .apply()
    }

    fun deleteLoginDataStore() {
        sharedPreferences.edit().remove(AppConstants.PREF_KEY_ACCESS_TOKEN).apply()
    }

    fun getDataStoreLoginId() : String {
        return sharedPreferences.getString(AppConstants.PREF_KEY_USER_ID,"") ?: ""
    }

    fun getDataStoreAccessToken() : String {
        return sharedPreferences.getString(AppConstants.PREF_KEY_ACCESS_TOKEN,"") ?: ""
    }

    fun hasLoginDataStore(): Boolean {
        LogUtil.log(
            "TAG",
            ": ${sharedPreferences.getString(AppConstants.PREF_KEY_ACCESS_TOKEN, "")}"
        )
        return sharedPreferences.getString(
            AppConstants.PREF_KEY_ACCESS_TOKEN,
            ""
        ) != "" && sharedPreferences.getString(AppConstants.PREF_KEY_USER_ID, "") != ""
    }

    fun saveLoginType(loginType: Int) {
        sharedPreferences.edit().putInt(AppConstants.PREF_KEY_LOGIN_TYPE, loginType).apply()
    }

    fun getLoginType() = sharedPreferences.getInt(AppConstants.PREF_KEY_LOGIN_TYPE, -1)

    fun getUUID(): String {
        return sharedPreferences.getString(PREF_KEY_UUID_KEY, "")!!
    }

    fun setUUID(uuid: String) {
        sharedPreferences.edit().putString(PREF_KEY_UUID_KEY, uuid).apply()
        AppConstants.UUID = uuid
    }

    fun getUserName(): String {
        return sharedPreferences.getString(PREF_KEY_CURRENT_USER_NAME, "")!!
    }

    fun setUserName(name: String) {
        sharedPreferences.edit().putString(PREF_KEY_CURRENT_USER_NAME, name).apply()
        AppConstants.NAME = name
    }

    fun getUserEmail(): String {
        return sharedPreferences.getString(PREF_KEY_CURRENT_USER_EMAIL, "")!!
    }

    fun setUserEmail(email: String) {
        sharedPreferences.edit().putString(PREF_KEY_CURRENT_USER_EMAIL, email).apply()
        AppConstants.EMAIL = email
    }

    fun getSubscribeTopicList(): List<String> {
        val topicsJson = sharedPreferences.getString(PREF_KEY_TOPIC_LIST, "[]")
        return GsonBuilder().create().fromJson<ArrayList<String>>(topicsJson, ArrayList::class.java)
    }

    fun setSubscribeTopic(topic: String) {
        val topicsJson = sharedPreferences.getString(PREF_KEY_TOPIC_LIST, "[]")
        val topics = GsonBuilder().create().fromJson<ArrayList<String>>(topicsJson, ArrayList::class.java)
        topics.add(topic)
        sharedPreferences.edit().putString(PREF_KEY_TOPIC_LIST, Gson().toJson(topics)).apply()
        sharedPreferences.edit().putBoolean(PREF_KEY_INITIAL, false).apply()
    }

    fun unsetSubscribeTopic(topic: String) {
        val topicsJson = sharedPreferences.getString(PREF_KEY_TOPIC_LIST, "[]")
        val topics = GsonBuilder().create().fromJson<ArrayList<String>>(topicsJson, ArrayList::class.java)
        topics.remove(topic)
        sharedPreferences.edit().putString(PREF_KEY_TOPIC_LIST, Gson().toJson(topics)).apply()
    }

    fun setDoNotShowPopup(id: Int) {
        val topicsJson = sharedPreferences.getString(PREF_KEY_DO_NOT_SHOW, "[]")
        val topics = GsonBuilder().create().fromJson<ArrayList<String>>(topicsJson, ArrayList::class.java)
        topics.add(id.toString())
        sharedPreferences.edit().putString(PREF_KEY_DO_NOT_SHOW, Gson().toJson(topics)).apply()
    }

    fun getDoNotShowPopupIdList(): List<String> {
        val topicsJson = sharedPreferences.getString(PREF_KEY_DO_NOT_SHOW, "[]")
        return GsonBuilder().create().fromJson<ArrayList<String>>(topicsJson, ArrayList::class.java)
    }

    fun setClosePopup(id: Int) {
        val topicsJson = sharedPreferences.getString(PREF_KEY_CLOSE, "[]")
        val topics = GsonBuilder().create().fromJson<ArrayList<String>>(topicsJson, ArrayList::class.java)
        topics.add(id.toString())
        sharedPreferences.edit().putString(PREF_KEY_CLOSE, Gson().toJson(topics)).apply()
    }

    fun getClosePopup(): List<String> {
        val topicsJson = sharedPreferences.getString(PREF_KEY_CLOSE, "[]")
        return GsonBuilder().create().fromJson<ArrayList<String>>(topicsJson, ArrayList::class.java)
    }

    fun removeAllClosePopup() {
        sharedPreferences.edit().putString(PREF_KEY_CLOSE, "[]").apply()
    }

    fun removeAllNotShowPopup() {
        sharedPreferences.edit().putString(PREF_KEY_DO_NOT_SHOW, "[]").apply()
    }

    fun setLastDate(date: String) {
        sharedPreferences.edit().putString(PREF_KEY_LAST_DATE, date).apply()
    }

    fun getLastDate(): String {
        return sharedPreferences.getString(PREF_KEY_LAST_DATE, "")?: ""
    }

    fun isInitialLogin(): Boolean {
        return sharedPreferences.getBoolean(PREF_KEY_INITIAL, true)

    }

    fun getAuthorityPopup(): Boolean {
        return sharedPreferences.getBoolean(PREF_KEY_AUTHORITY_POP_UP, false)
    }

    fun setAuthorityPopup(clear: Boolean) {
        sharedPreferences.edit().putBoolean(PREF_KEY_AUTHORITY_POP_UP, clear).apply()
    }

    fun getGuidePopupShow() {
        sharedPreferences.getBoolean(PREF_KEY_GUIDE_POP_UP_SHOW, false)
    }

    fun setGuidePopupShow() {
        sharedPreferences.edit().putBoolean(PREF_KEY_GUIDE_POP_UP_SHOW, true).apply()
    }
}