package com.chairul.githubuser.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.chairul.githubuser.db.DatabaseContract.UserColumns.Companion.TABLE_NAME

internal class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION)  {
    companion object {
        private const val DATABASE_NAME = "dbuserapp"
        private const val DATABASE_VERSION = 7
        private val SQL_CREATE_TABLE_USER = "CREATE TABLE $TABLE_NAME" +
                " (${DatabaseContract.UserColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                " ${DatabaseContract.UserColumns.LOGIN} ," +
                " ${DatabaseContract.UserColumns.NAME} ," +
                " ${DatabaseContract.UserColumns.COMPANY} ,"+
                " ${DatabaseContract.UserColumns.AVATAR_URL},"+
                " ${DatabaseContract.UserColumns.ISFAVORITE} )"
               /* " ${DatabaseContract.UserColumns.TYPE} ," +
                " ${DatabaseContract.UserColumns.NAME} ," +
                " ${DatabaseContract.UserColumns.COMPANY} ," +
                " ${DatabaseContract.UserColumns.BLOG} ," +
                " ${DatabaseContract.UserColumns.LOCATION} ," +
                " ${DatabaseContract.UserColumns.ORGANIZATION} ," +
                " ${DatabaseContract.UserColumns.EMAIL} ," +
                " ${DatabaseContract.UserColumns.AVATAR_URL} ," +
                " ${DatabaseContract.UserColumns.FOLLOWERS} ," +
                " ${DatabaseContract.UserColumns.FOLLOWING} )"

                */

    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_TABLE_USER)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }
}