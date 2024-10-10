package com.example.kkbimage.ui.main.model

import com.google.gson.annotations.SerializedName

data class SearchImageData(
    @SerializedName("collection") val collection: String,
    @SerializedName("thumbnail_url") val thumbnail_url: String,
    @SerializedName("image_url") val image_url: String,
    @SerializedName("width") val width: Int,
    @SerializedName("height") val height: Int,
    @SerializedName("display_sitename") val display_sitename: String,
    @SerializedName("doc_url") val doc_url: String,
    @SerializedName("datetime") val datetime: String
)