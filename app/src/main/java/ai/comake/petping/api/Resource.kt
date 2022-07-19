package ai.comake.petping.api

import ai.comake.petping.data.vo.ErrorResponse
import okhttp3.ResponseBody

sealed class Resource<out T> {
    data class Success<out T>(val value: T) : Resource<T>()
    data class Failure(
        val errorCode: Int?,
        val errorBody: ResponseBody?
    ) : Resource<Nothing>()

    data class Error(
        val errorCode: Int?,
        val errorBody: ErrorResponse?
    ) : Resource<Nothing>()

//    data class ServerError(
//        val errorCode: Int?,
//        val errorBody: ServerErrorResponse?
//    ) : Resource<Nothing>()
}

