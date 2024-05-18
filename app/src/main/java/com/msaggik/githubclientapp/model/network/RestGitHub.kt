package com.msaggik.githubclientapp.model.network

import com.msaggik.githubclientapp.model.entities.item.follower.Follower
import com.msaggik.githubclientapp.model.entities.item.repositories.Repos
import com.msaggik.githubclientapp.model.entities.itemsearch.ResponseServerUsers
import com.msaggik.githubclientapp.model.entities.item.User
import com.msaggik.githubclientapp.model.entities.oauth.Token
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
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
    fun showRepositories(
        @Path("user") user: String,
        @Query("page") page: Int
    ): Call<List<Repos>>

//    @Headers("Accept: application/json")
//    @FormUrlEncoded
//    @POST("/login/oauth/access_token/")
//    fun getAccessToken(
//        @Field("client_id") clientId: String,
//        @Field("client_secret") clientSecret: String,
//        @Field("code") code: String
//    ): Call<Token>

    @Headers("Accept: application/json")
    @POST("/login/oauth/access_token/")
    fun getAccessToken(
        @Query("client_id") clientId: String,
        @Query("client_secret") clientSecret: String,
        @Query("code") code: String
    ): Call<Token>

    @Headers("Accept: application/vnd.github+json")
    @DELETE("/applications/{client_id}/grant")
    fun getDeleteToken(
        @Path("client_id") clientId: String,
        @Body request: Token,
    ) // successful server response 204

    @GET("/search/users")
    fun searchItemOauth(@Header("Authorization") token: String, @Query("q") text: String): Call<ResponseServerUsers>

    @GET("/users/{user}/followers?per_page=100")
    fun showFollowersOauth(@Header("Authorization") token: String, @Path("user") user: String): Call<List<Follower>>

    @GET("/users/{user}")
    fun showUserOauth(@Header("Authorization") token: String, @Path("user") user: String): Call<User>

    @GET("/users/{user}/repos?sort=updated&per_page=100")
    fun showRepositoriesOauth(
        @Header("Authorization") token: String,
        @Path("user") user: String,
        @Query("page") page: Int
    ): Call<List<Repos>>

}