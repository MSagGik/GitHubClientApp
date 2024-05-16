package com.msaggik.githubclientapp.util.entities

import com.google.gson.annotations.SerializedName

data class ResponseServerUsers(
    @SerializedName("total_count") var totalCount: Int? = null,
    @SerializedName("incomplete_results") var incompleteResults: Boolean? = null,
    @SerializedName("items") var items: ArrayList<Item> = arrayListOf()
)
