package com.example.kkbimage.ui.main.model

import com.example.kkbimage.BuildConfig
import retrofit2.Response
import retrofit2.http.*

interface KakaoApi {

    @GET("search/image?sort=recency&size=10")
    suspend fun searchImage(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Header("Authorization") token: String = "KakaoAK "+BuildConfig.KAKAO_REST_API_KEY
    ): Response<SearchImageDto>


    @GET("search/vclip?sort=recency&size=10")
    suspend fun searchVideo(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Header("Authorization") token: String = "KakaoAK "+BuildConfig.KAKAO_REST_API_KEY
    ): Response<SearchVideoDto>
}