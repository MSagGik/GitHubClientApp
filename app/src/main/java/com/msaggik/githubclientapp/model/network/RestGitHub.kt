package com.msaggik.githubclientapp.model.network

import android.renderscript.Script
import com.msaggik.githubclientapp.model.entities.item.follower.Follower
import com.msaggik.githubclientapp.model.entities.item.repositories.Repos
import com.msaggik.githubclientapp.model.entities.itemsearch.ResponseServerUsers
import com.msaggik.githubclientapp.model.entities.item.User
import com.msaggik.githubclientapp.model.entities.oauth.Token
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface RestGitHub {

    companion object{
        fun createRetrofitObject(baseUrl: String) : Retrofit {
            return  Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        fun createRetrofitObjectTest(baseUrl: String): Retrofit {
            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(OkHttpClient.Builder().
                    addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                    .build())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }

    // OAuth
    @Headers("Accept: application/json")
    @POST("/login/oauth/access_token/")
    fun getAccessToken(
        @Query("client_id") clientId: String,
        @Query("client_secret") clientSecret: String,
        @Query("code") code: String
    ): Call<Token>

    @Headers("Accept: application/json")
    @HTTP(method = "DELETE", path = "/applications/{client_id}/grant", hasBody = true)
    fun logOut(
        @Header("Authorization") token: String,
        @Body request: Token,
        @Path("client_id") clientId: String
    ) : Call<String>

    // Profile
    @Headers("Content-Type: application/json")
    @GET("/user")
    fun getUserInfo(
        @Header("Authorization") token: String
    ): Call<User>

    // Search
    @GET("/search/users")
    fun searchItem(@Query("q") text: String): Call<ResponseServerUsers>

    @GET("/search/users")
    fun searchItemOauth(@Header("Authorization") token: String, @Query("q") text: String): Call<ResponseServerUsers>

    // Followers
    @GET("/users/{user}/followers?per_page=100")
    fun showFollowers(@Path("user") user: String): Call<List<Follower>>

    @GET("/users/{user}/followers?per_page=100")
    fun showFollowersOauth(@Header("Authorization") token: String, @Path("user") user: String): Call<List<Follower>>

    // User
    @GET("/users/{user}")
    fun showUser(@Path("user") user: String): Call<User>

    @GET("/users/{user}")
    fun showUserOauth(@Header("Authorization") token: String, @Path("user") user: String): Call<User>

    @GET("/users/{user}/repos?sort=updated")
    fun showRepositories(
        @Path("user") user: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): Call<List<Repos>>

    @GET("/users/{user}/repos?sort=updated")
    fun showRepositoriesOauth(
        @Header("Authorization") token: String,
        @Path("user") user: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): Call<List<Repos>>

}