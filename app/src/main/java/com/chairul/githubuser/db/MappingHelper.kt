package com.chairul.githubuser.db

import android.database.Cursor
import android.util.Log
import com.chairul.githubuser.entity.User

object MappingHelper {


    fun mapCursorToArrayList(notesCursor: Cursor?): ArrayList<User> {
        val notesList = ArrayList<User>()
        notesCursor?.apply {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(DatabaseContract.UserColumns._ID))
                val login = getString(getColumnIndexOrThrow(DatabaseContract.UserColumns.LOGIN))
                val name = getString(getColumnIndexOrThrow(DatabaseContract.UserColumns.NAME))
                val company = getString(getColumnIndexOrThrow(DatabaseContract.UserColumns.COMPANY))
                val avatar_url = getString(getColumnIndexOrThrow(DatabaseContract.UserColumns.AVATAR_URL))
                Log.v("ADX","getColumnIndexOrThrow(DatabaseContract.UserColumns.ISFAVORITE) "+getString(getColumnIndexOrThrow(DatabaseContract.UserColumns.ISFAVORITE) ))
                val isFavorite = getString(getColumnIndexOrThrow(DatabaseContract.UserColumns.ISFAVORITE))
                notesList.add(
                    User(
                        id,
                        login,
                        name,
                        company,
                        avatar_url,
                        isFavorite
                    )
                )

            }
        }
        return notesList
    }

    fun mapCursorToObject(notesCursor: Cursor?): User {
        lateinit var user: User
        notesCursor?.apply {
            moveToFirst()
            val id = getInt(getColumnIndexOrThrow(DatabaseContract.UserColumns._ID))
            val login = getString(getColumnIndexOrThrow(DatabaseContract.UserColumns.LOGIN))
            val name = getString(getColumnIndexOrThrow(DatabaseContract.UserColumns.NAME))
            val company = getString(getColumnIndexOrThrow(DatabaseContract.UserColumns.COMPANY))
            val avatar_url = getString(getColumnIndexOrThrow(DatabaseContract.UserColumns.AVATAR_URL))
            val isFavorite = getString(getColumnIndexOrThrow(DatabaseContract.UserColumns.ISFAVORITE))
            user = User(
                id,
                login,
                name,
                company,
                avatar_url,
                isFavorite
            )
        }
        return user
    }
}