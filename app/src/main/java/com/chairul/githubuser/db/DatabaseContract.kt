package com.chairul.githubuser.db

import android.net.Uri
import android.provider.BaseColumns

object DatabaseContract {

    const val AUTHORITY = "com.chairul.githubuser"
    const val SCHEME = "content"

    class UserColumns : BaseColumns {
        companion object {
            const val TABLE_NAME = "user_table"
            const val _ID = "_id"
            const val LOGIN = "login"
            const val NAME = "name"
            const val COMPANY = "company"
            const val AVATAR_URL = "avatar_url"
            const val ISFAVORITE= "isFavorite"
            /*const val TYPE = "type"
            const val NAME = "name"
            const val COMPANY = "company"
            const val BLOG = "blog"
            const val LOCATION = "location"
            const val ORGANIZATION = "organization"
            const val EMAIL = "email"
            const val AVATAR_URL = "avatar_url"
            const val FOLLOWERS = "followers"
            const val FOLLOWING = "following"

             */

            val CONTENT_URI: Uri = Uri.Builder().scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(TABLE_NAME)
                .build()
        }
    }
}