package com.hzzh.wallft

import android.app.Service
import android.app.WallpaperManager
import android.content.Intent
import android.graphics.Bitmap
import android.os.Binder
import android.os.IBinder
import android.util.Log


class WallPaperService : Service() {
    private var current = 0 //当前壁纸下标
//    private val papers = arrayOf(com.hzzh.wallft.R.drawable.apple_pic, com.hzzh.wallft.R.drawable.banana_pic, com.hzzh.wallft.R.drawable.pear_pic, com.hzzh.wallft.R.drawable.strawberry_pic)
    private val papersList = ArrayList<Bitmap>()
    private var wManager: WallpaperManager? = null //定义WallpaperManager服务

    private val mBinder = addBinder()

    override fun onCreate() {
        super.onCreate()
        wManager = WallpaperManager.getInstance(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (current >= papersList.size) current = 0
        try {
//            wManager!!.setResource(papersList[current++])
            wManager!!.setBitmap(papersList.get(current++))
            Log.d("Wallft", "shiftshift")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return START_STICKY
    }

    inner class addBinder : Binder() {

        fun addPaper(bitmap: Bitmap){
          papersList.add(bitmap)
        }
    }
    override fun onBind(intent: Intent?): IBinder? {
        return mBinder
    }

}