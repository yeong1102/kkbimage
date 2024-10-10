package com.example.kkbimage

import com.example.kkbimage.ui.main.model.KakaoApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {

    private const val KAKAO_API = "Kakao_API"
    private const val KAKAO_OK_HTTP = "Kakao_OkHttp"


    @Provides
    fun provideAgoraChannelStatisticsApi(@Named(KAKAO_API) retrofit: Retrofit): KakaoApi =
        retrofit.create(KakaoApi::class.java)

    @Provides
    @Singleton
    @Named(KAKAO_API)
    fun provideKakaoRetrofit(@Named(KAKAO_OK_HTTP) okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.KAKAO_BASE_URL+"/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
            .build()

    @Provides
    @Singleton
    @Named(KAKAO_OK_HTTP)
    fun provideKakaoOkHttpClient(
        @Named("logging_interceptor") loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient =
        OkHttpClient.Builder()
            .run {
                connectTimeout(20, TimeUnit.SECONDS)
                writeTimeout(20, TimeUnit.SECONDS)
                readTimeout(20, TimeUnit.SECONDS)
                build()
            }

    @Provides
    @Singleton
    @Named("logging_interceptor")
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
}