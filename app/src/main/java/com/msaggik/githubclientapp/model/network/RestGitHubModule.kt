package com.msaggik.githubclientapp.model.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RestGitHubModule {

    fun createRetrofitObject(baseUrl: String) : Retrofit {
        return  Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}