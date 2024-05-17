package com.msaggik.githubclientapp.model.network

import com.msaggik.githubclientapp.model.entities.item.follower.Follower
import com.msaggik.githubclientapp.model.entities.item.repositories.Repos
import com.msaggik.githubclientapp.model.entities.itemsearch.ResponseServerUsers
import com.msaggik.githubclientapp.model.entities.item.User
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

    @GET("/users/{user}/repos?sort=updated&per_page=5")
    fun showRepositories(@Path("user") user: String, @Query("page") page: Int): Call<List<Repos>>

}