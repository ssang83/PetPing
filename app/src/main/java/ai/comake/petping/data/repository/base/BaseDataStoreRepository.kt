package ai.comake.petping.data.repository.base

import kotlinx.coroutines.flow.Flow

/**
 * android-petping-2
 * Class: DataStoreRepository
 * Created by cliff on 2022/05/10.
 *
 * Description:
 */
interface BaseDataStoreRepository {

    suspend fun update(key : String, value : Boolean)

    suspend fun update(key : String, value : String)

    suspend fun update(key : String, value : Int)

    suspend fun update(key : String, value : Double)

    suspend fun update(key : String, value : Long)

    fun getBoolean(key: String) : Flow<Boolean>

    fun getString(key: String) : Flow<String>

    fun getInteger(key: String) : Flow<Int>

    fun getDouble(key: String) : Flow<Double>

    fun getLong(key: String) : Flow<Long>

//    suspend fun getBoolean(key: String) : Boolean
//
//    suspend fun getString(key: String) : String
//
//    suspend fun getInteger(key: String) : Int
//
//    suspend fun getDouble(key: String) : Double
//
//    suspend fun getLong(key: String) : Long

    suspend fun clear()
}