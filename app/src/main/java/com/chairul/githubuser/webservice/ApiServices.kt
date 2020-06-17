package com.chairul.githubuser.webservice

import com.chairul.githubuser.BuildConfig
import com.chairul.githubuser.entity.SearchUser
import com.chairul.githubuser.entity.UserDTO
import com.chairul.githubuser.entity.UserURL
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiServices {

    @GET("users")
    @Headers("Authorization: token  ${BuildConfig.API_KEY}")
    fun getAllUsers(): Call<ArrayList<UserURL>>

    @GET("users/{username}")
    @Headers("Authorization: token  ${BuildConfig.API_KEY}")
    fun getUserDetail(@Path("username") username : String): Call<UserDTO>

    @GET("users/{username}/followers")
    @Headers("Authorization: token  ${BuildConfig.API_KEY}")
    fun getFollower(@Path("username") username : String): Call<ArrayList<UserURL>>

    @GET("users/{username}/following")
    @Headers("Authorization: token  ${BuildConfig.API_KEY}")
    fun getFollowing(@Path("username") username : String): Call<ArrayList<UserURL>>

    @GET("search/users?")
    @Headers("Authorization: token  ${BuildConfig.API_KEY}")
    fun getSearchUser(@Query("q") q : String): Call<SearchUser>
}