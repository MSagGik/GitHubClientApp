package com.msaggik.githubclientapp.util.network

import com.msaggik.githubclientapp.util.entities.Follower
import com.msaggik.githubclientapp.util.entities.Item
import com.msaggik.githubclientapp.util.entities.ResponseServerUsers
import com.msaggik.githubclientapp.util.entities.User
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RestGitHub {

    @GET("/search/users")
    fun searchItem(@Query("q") text: String): Call<ResponseServerUsers>

    @GET("/users/{user}/followers?per_page=100")
    fun showFollowers(@Path("user") user: String): Call<List<Follower>>

    @GET("/users/{user}")
    fun showUser(@Path("user") user: String): Call<User>

}