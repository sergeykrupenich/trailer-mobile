package com.siarheikrupenich.trailermobile.core.network.di

import com.siarheikrupenich.trailermobile.core.network.errorhandling.ResultCallAdapterFactory
import com.google.gson.Gson
import com.siarheikrupenich.trailermobile.core.network.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
internal class NetworkModule {

    companion object {
        private const val TIMEOUT = 90L

        @Singleton
        @Provides
        fun provideGson(): Gson = Gson()

        @Singleton
        @Provides
        fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor =
            HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                    else HttpLoggingInterceptor.Level.NONE
            }

        @Singleton
        @Provides
        fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor) = OkHttpClient.Builder()
            .readTimeout(TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val rawToken = BuildConfig.BEARER_TOKEN
                val authHeader = if (rawToken.isBlank()) "" else if (rawToken.startsWith("Bearer ", ignoreCase = true)) {
                    rawToken
                } else {
                    "Bearer $rawToken"
                }
                val requestBuilder = chain.request().newBuilder()
                    .addHeader("accept", "application/json")
                if (authHeader.isNotBlank()) {
                    requestBuilder.addHeader("Authorization", authHeader)
                }
                chain.proceed(requestBuilder.build())
            }
            .addInterceptor(loggingInterceptor)
            .build()

        @Singleton
        @Provides
        fun provideRetrofit(
            gson: Gson,
            okHttpClient: OkHttpClient
        ): Retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(ResultCallAdapterFactory(gson))
            .build()
    }
}
