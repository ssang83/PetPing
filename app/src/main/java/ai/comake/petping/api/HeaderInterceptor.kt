package ai.comake.petping.api


import okhttp3.Interceptor
import okhttp3.Response

internal class HeaderInterceptor(private val userAgent: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(
            chain.request().newBuilder()
                .header("User-Agent", userAgent)
                .build()
        )
    }
}