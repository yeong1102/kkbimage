package com.example.kkbimage.ui.main.model

import com.google.gson.annotations.SerializedName

data class SearchVideoData(
    @SerializedName("title") val title: String,
    @SerializedName("url") val url: String,
    @SerializedName("datetime") val datetime: String,
    @SerializedName("play_time") val play_time: Int,
    @SerializedName("thumbnail") val thumbnail: String,
    @SerializedName("author") val author: String
)
