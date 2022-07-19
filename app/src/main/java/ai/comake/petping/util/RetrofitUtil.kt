package ai.comake.petping.util

import ai.comake.petping.AppConstants.PETPING_URL
import ai.comake.petping.data.vo.ErrorResponse
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

fun getErrorBodyConverter(): Converter<ResponseBody, ErrorResponse> {
    val retrofit = Retrofit.Builder()
        .baseUrl(PETPING_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    return retrofit.responseBodyConverter(ErrorResponse::class.java, arrayOf())
}

fun <T> getErrorBody(klass: Class<T>): Converter<ResponseBody, T> {
    val retrofit = Retrofit.Builder()
        .baseUrl(PETPING_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    return retrofit.responseBodyConverter(klass, arrayOf())
}