package ai.comake.petping.data.preference

import ai.comake.petping.AppConstants
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.GsonBuilder

/**
 * android-petping-2
 * Class: PreferenceImpl
 * Created by cliff on 2022/06/28.
 *
 * Description:
 */
class PreferenceImpl(
    val context: Context,
    prefFileName: String
) : Preference {

    private val mPref: SharedPreferences =
        context.getSharedPreferences(prefFileName, Context.MODE_PRIVATE)

    private val PREF_KEY_ACCESS_TOKEN = "ACCESS_TOKEN"
    private val PREF_KEY_CURRENT_USER_EMAIL = "USER_EMAIL"
    private val PREF_KEY_CURRENT_USER_ID = "USER_ID"
    private val PREF_KEY_CURRENT_USER_NAME = "USER_NAME"
    private val PREF_KEY_TOPIC_LIST = "TOPIC_LIST"
    private val PREF_KEY_INITIAL = "TOPIC_INTIIAL"
    private val PREF_KEY_DO_NOT_SHOW = "DO_NOT_SHOW_POP_UP"
    private val PREF_KEY_CLOSE = "CLOSE_POP_UP"
    private val PREF_KEY_LAST_DATE = "LAST_DATE"
    private val PREF_KEY_UUID_KEY = "UUID_KEY"

    override fun getUUID(): String {
        return mPref.getString(PREF_KEY_UUID_KEY, "")!!
    }

    override fun setUUID(uuid: String) {
        mPref.edit().putString(PREF_KEY_UUID_KEY, uuid).apply()
        AppConstants.UUID = uuid
    }

    override fun getAccessToken(): String {
        return mPref.getString(PREF_KEY_ACCESS_TOKEN, "")!!
    }

    override fun setAccessToken(token: String) {
        mPref.edit().putString(PREF_KEY_ACCESS_TOKEN, token).apply()
        AppConstants.AUTH_KEY = token

        if(isInitialLogin()) {

        }
    }

    override fun getUserName(): String {
        return mPref.getString(PREF_KEY_CURRENT_USER_NAME, "")!!
    }

    override fun setUserName(name: String) {
        mPref.edit().putString(PREF_KEY_CURRENT_USER_NAME, name).apply()
        AppConstants.NAME = name
    }

    override fun getUserId(): String {
        return mPref.getString(PREF_KEY_CURRENT_USER_ID, "")!!
    }

    override fun setUserId(id: String) {
        mPref.edit().putString(PREF_KEY_CURRENT_USER_ID, id).apply()
        AppConstants.ID = id
    }

    override fun getUserEmail(): String {
        return mPref.getString(PREF_KEY_CURRENT_USER_EMAIL, "")!!
    }

    override fun setUserEmail(email: String) {
        mPref.edit().putString(PREF_KEY_CURRENT_USER_EMAIL, email).apply()
        AppConstants.EMAIL = email
    }

    override fun getSubscribeTopicList(): List<String> {
        val topicsJson = mPref.getString(PREF_KEY_TOPIC_LIST, "[]")
        return GsonBuilder().create().fromJson<ArrayList<String>>(topicsJson, ArrayList::class.java)
    }

    override fun setSubscribeTopic(topic: String) {
        val topicsJson = mPref.getString(PREF_KEY_TOPIC_LIST, "[]")
        val topics = GsonBuilder().create().fromJson<ArrayList<String>>(topicsJson, ArrayList::class.java)
        topics.add(topic)
        mPref.edit().putString(PREF_KEY_TOPIC_LIST, Gson().toJson(topics)).apply()
        mPref.edit().putBoolean(PREF_KEY_INITIAL, false).apply()
    }

    override fun unsetSubscribeTopic(topic: String) {
        val topicsJson = mPref.getString(PREF_KEY_TOPIC_LIST, "[]")
        val topics = GsonBuilder().create().fromJson<ArrayList<String>>(topicsJson, ArrayList::class.java)
        topics.remove(topic)
        mPref.edit().putString(PREF_KEY_TOPIC_LIST, Gson().toJson(topics)).apply()
    }

    override fun setDoNotShowPopup(id: Int) {
        val topicsJson = mPref.getString(PREF_KEY_DO_NOT_SHOW, "[]")
        val topics = GsonBuilder().create().fromJson<ArrayList<String>>(topicsJson, ArrayList::class.java)
        topics.add(id.toString())
        mPref.edit().putString(PREF_KEY_DO_NOT_SHOW, Gson().toJson(topics)).apply()
    }

    override fun getDoNotShowPopupIdList(): List<String> {
        val topicsJson = mPref.getString(PREF_KEY_DO_NOT_SHOW, "[]")
        return GsonBuilder().create().fromJson<ArrayList<String>>(topicsJson, ArrayList::class.java)
    }

    override fun setClosePopup(id: Int) {
        val topicsJson = mPref.getString(PREF_KEY_CLOSE, "[]")
        val topics = GsonBuilder().create().fromJson<ArrayList<String>>(topicsJson, ArrayList::class.java)
        topics.add(id.toString())
        mPref.edit().putString(PREF_KEY_CLOSE, Gson().toJson(topics)).apply()
    }


    override fun getClosePopup(): List<String> {
        val topicsJson = mPref.getString(PREF_KEY_CLOSE, "[]")
        return GsonBuilder().create().fromJson<ArrayList<String>>(topicsJson, ArrayList::class.java)
    }

    override fun removeAllClosePopup() {
        mPref.edit().putString(PREF_KEY_CLOSE, "[]").apply()
    }

    override fun removeAllNotShowPopup() {
        mPref.edit().putString(PREF_KEY_DO_NOT_SHOW, "[]").apply()
    }

    override fun setLastDate(date: String) {
        mPref.edit().putString(PREF_KEY_LAST_DATE, date).apply()
    }

    override fun getLastDate(): String {
        return mPref.getString(PREF_KEY_LAST_DATE, "")?: ""
    }

    override fun isInitialLogin(): Boolean {
        return mPref.getBoolean(PREF_KEY_INITIAL, true)

    }

    override fun logout() {
        mPref.edit().clear().apply()
    }
}