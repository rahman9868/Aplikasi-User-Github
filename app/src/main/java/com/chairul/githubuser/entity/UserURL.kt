package com.chairul.githubuser.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class UserResponse(
    val user: List<UserURL>
)

data class UserURL(
    val login: String,
    val avatar_url: String,
    val url: String,
    val html_url: String,
    val followers_url: String,
    val following_url: String
)

@Parcelize
data class UserDTO(
    var id:Int,
    var login: String,
    var type: String?,
    var name: String?,
    var company: String?,
    var blog: String?,
    var location: String?,
    var organization: String?,
    var email: String?,
    var avatar_url: String?,
    var followers: Int?,
    var following: Int?
) : Parcelable

data class SearchUser(
    val total_count: Int,
    val items: ArrayList<User>
)

@Parcelize
data class User(
    var id:Int = 0,
    var login: String,
    var name: String?,
    var company: String?,
    var avatar_url: String?,
    var isFavorite: String
): Parcelable
