package ai.comake.petping.data.repository

import ai.comake.petping.data.repository.base.BaseDataStoreRepository
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

/**
 * android-petping-2
 * Class: DataStoreRepository
 * Created by cliff on 2022/05/10.
 *
 * Description:
 */
class DataStoreRepository @Inject constructor(private val dataStore: DataStore<Preferences>)
    : BaseDataStoreRepository {

    //region CRUD Operation
    override suspend fun update(key: String, value: Boolean) {
        dataStore.edit { preference ->
            preference.set(booleanPreferencesKey(key), value)
        }
    }

    override suspend fun update(key: String, value: String) {
        dataStore.edit { preference ->
            preference.set(stringPreferencesKey(key), value)
        }
    }

    override suspend fun update(key: String, value: Int) {
        dataStore.edit { preference ->
            preference.set(intPreferencesKey(key), value)
        }
    }

    override suspend fun update(key: String, value: Double) {
        dataStore.edit { preference ->
            preference.set(doublePreferencesKey(key), value)
        }
    }

    override suspend fun update(key: String, value: Long) {
        dataStore.edit { preference ->
            preference.set(longPreferencesKey(key), value)
        }
    }

    override fun getBoolean(key: String): Flow<Boolean> {
        return dataStore.data.catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preference ->
            preference.get(booleanPreferencesKey(key)) ?: false
        }
    }

    override fun getString(key: String): Flow<String> {
        return dataStore.data.catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preference ->
            preference.get(stringPreferencesKey(key)) ?: "Nil"
        }
    }

    override fun getInteger(key: String): Flow<Int> {
        return dataStore.data.catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preference ->
            preference.get(intPreferencesKey(key)) ?: 0
        }
    }

    override fun getDouble(key: String): Flow<Double> {
        return dataStore.data.catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preference ->
            preference.get(doublePreferencesKey(key)) ?: 0.00
        }
    }

    override fun getLong(key: String): Flow<Long> {
        return dataStore.data.catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preference ->
            preference.get(longPreferencesKey(key)) ?: 0L
        }
    }

//    override suspend fun getBoolean(key: String): Boolean {
//        return try {
//            val preferencesKey = booleanPreferencesKey(key)
//            val preferences = dataStore.data.first()
//            preferences[preferencesKey] ?: false
//        } catch (e: Exception) {
//            e.printStackTrace()
//            false
//        }
//    }
//
//    override suspend fun getString(key: String): String {
//        return try {
//            val preferencesKey = stringPreferencesKey(key)
//            val preferences = dataStore.data.first()
//            preferences[preferencesKey] ?: "Nil"
//        } catch (e: Exception) {
//            e.printStackTrace()
//            ""
//        }
//    }
//
//    override suspend fun getInteger(key: String): Int {
//        return try {
//            val preferencesKey = intPreferencesKey(key)
//            val preferences = dataStore.data.first()
//            preferences[preferencesKey] ?: -1
//        } catch (e: Exception) {
//            e.printStackTrace()
//            -1
//        }
//    }
//
//    override suspend fun getDouble(key: String): Double {
//        return try {
//            val preferencesKey = doublePreferencesKey(key)
//            val preferences = dataStore.data.first()
//            preferences[preferencesKey] ?: 0.00
//        } catch (e: Exception) {
//            e.printStackTrace()
//            0.00
//        }
//    }
//
//    override suspend fun getLong(key: String): Long {
//        return try {
//            val preferencesKey = longPreferencesKey(key)
//            val preferences = dataStore.data.first()
//            preferences[preferencesKey] ?: 0L
//        } catch (e: Exception) {
//            e.printStackTrace()
//            0L
//        }
//    }

    override suspend fun clear() {
        dataStore.edit { preference ->
            preference.clear()
        }
    }
}