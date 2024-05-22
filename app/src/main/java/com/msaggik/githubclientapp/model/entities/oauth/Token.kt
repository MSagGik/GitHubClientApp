package com.msaggik.githubclientapp.model.entities.oauth

import com.google.gson.annotations.SerializedName

data class Token(
    @SerializedName("access_token")
    val accessToken:String? = null,
    @SerializedName("scope")
    val scope:String? = null,
    @SerializedName("token_type")
    val tokenType:String? = null
)
