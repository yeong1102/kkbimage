package com.example.kkbimage.ui.main.model

import com.example.kkbimage.ui.main.SearchMeta
import com.google.gson.annotations.SerializedName

data class SearchImageDto(
    @SerializedName("meta") val meta: SearchMeta,
    @SerializedName("documents") val documents: List<SearchImageData>
)
