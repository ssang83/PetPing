package ai.comake.petping.data.repository.base

import ai.comake.petping.api.Resource
import ai.comake.petping.data.vo.*
import ai.comake.petping.util.LogUtil
import ai.comake.petping.util.getErrorBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import retrofit2.HttpException
import java.net.UnknownHostException

abstract class BaseRepository {
    suspend fun <T> safeApiCall(apiCall: suspend () -> T): Resource<T> {
        return withContext(Dispatchers.IO) {
            try {
                return@withContext Resource.Success(apiCall.invoke())
            } catch (throwable: Throwable) {
                when (throwable) {
                    is HttpException -> {
                        try {
                            when (throwable.code()) {
                                400 -> {
                                    val errorResponse =
                                        getErrorBody(ErrorResponse::class.java).convert(
                                            throwable.response()?.errorBody()
                                        )

                                    Resource.Error(
                                        throwable.code(),
                                        errorResponse
                                    )
                                }
                                //로그아웃
                                401 -> {
                                    EventBus.getDefault().post(LogoutEvent(throwable.code()))
                                    Resource.Failure(null, null)
                                }
                                500 -> {
                                    Resource.Error(
                                        throwable.code(),
                                        null
                                    )
                                }
                                else -> {
                                    Resource.Failure(null, null)
                                }
                            }
                        } catch (e: Exception) {
                            Resource.Failure(null, null)
                        }
                    }
                    is UnknownHostException -> {
                        LogUtil.log("TAG", "UnknownHostException}")
                        EventBus.getDefault().post(NetworkErrorEvent(apiCall))
                        Resource.Failure(null, null)
                    }

                    else -> {
                        LogUtil.log("TAG", "StackTraceException ${throwable.printStackTrace()}")
                        Resource.Failure(null, null)
                    }
                }
            }
        }
    }
}