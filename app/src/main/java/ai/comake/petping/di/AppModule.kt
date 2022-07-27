package ai.comake.petping.di

import ai.comake.petping.AppConstants.PETPING_URL
import ai.comake.petping.BuildConfig
import ai.comake.petping.api.HeaderInterceptor
import ai.comake.petping.api.WebService
import ai.comake.petping.util.SharedPreferencesManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    private const val CONNECT_TIMEOUT = 10L
    private const val WRITE_TIMEOUT = 10L
    private const val READ_TIMEOUT = 10L

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val userAgent = "${BuildConfig.APPLICATION_ID}/${BuildConfig.VERSION_NAME} " +
                "(Android ${Build.VERSION.RELEASE}; ${Build.MODEL}; ${Build.BRAND})"
        return OkHttpClient.Builder().apply {
            protocols(Collections.singletonList(Protocol.HTTP_1_1))
            connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            retryOnConnectionFailure(true)
            addInterceptor(HeaderInterceptor(userAgent))
            addNetworkInterceptor(HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BODY
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
            })
        }.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(PETPING_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideWebService(retrofit: Retrofit): WebService {
        return retrofit.create(WebService::class.java)
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("pref_petping", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideSharedPreferencesManager(sharedPreferences: SharedPreferences) =
        SharedPreferencesManager(sharedPreferences)


//    @Provides
//    @Singleton
//    fun provideDataStore(application: Application): DataStore<Preferences> {
//        return PreferenceDataStoreFactory.create {
//            application.preferencesDataStoreFile("petping")
//        }
//    }
//
//    @Provides
//    @Singleton
//    fun provideDataStoreRepository(dataStore: DataStore<Preferences>): BaseDataStoreRepository {
//        return DataStoreRepository(dataStore)
//    }
}