package com.hzzh.wallft

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class WallpapersDatabase(val context: Context, name: String, version: Int) : SQLiteOpenHelper(context, name, null, version) {

    private val createWallpaper = "create table Wallpaper (" + "filename text," + "image blob)"

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(createWallpaper)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("Not yet implemented")
    }
}