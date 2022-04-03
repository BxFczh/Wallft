package com.hzzh.wallft

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.*
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.hzzh.wallft.databinding.ActivityMainBinding
import java.io.ByteArrayOutputStream
import java.io.Closeable
import java.io.InputStream
import java.sql.Blob

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val wallpaperList = ArrayList<Wallpaper>()

    val uriPathHelper = URIPathHelper()

    private var timer : Long = 0
    private var aManager: AlarmManager? = null
    private var pi: PendingIntent? = null

    lateinit var addBinder: WallPaperService.addBinder
    private val connection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            addBinder = service as WallPaperService.addBinder
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            TODO("Not yet implemented")
        }
    }


    private val dbHelper = WallpapersDatabase(this, "Wallpapers.db", 1)

    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val timerSaver = getSharedPreferences("data", Context.MODE_PRIVATE)
        timer = timerSaver.getLong("timer", 60000)

        val db = dbHelper.writableDatabase

        val intent1 = Intent(this, WallPaperService::class.java)
        bindService(intent1, connection, Context.BIND_AUTO_CREATE )

        val cursor = db.query("Wallpaper", null, null, null, null, null, null, null)
        if (cursor.moveToFirst()) {
            do {
                val filename = cursor.getString(cursor.getColumnIndex("filename"))
                val image = cursor.getBlob(cursor.getColumnIndex("image"))
                val bitmap = BitmapFactory.decodeByteArray(image, 0, image.size)
                wallpaperList.add(Wallpaper(filename, bitmap))
            } while (cursor.moveToNext())
        }
        cursor.close()

        val layoutManager = GridLayoutManager(this, 3)
        binding.recyclerView.layoutManager = layoutManager
        wallpaperAdapter()

        aManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, WallPaperService::class.java)
        pi = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_MUTABLE)
        binding.switchButton.setOnClickListener {
            for (wallpaper in wallpaperList) {
                addBinder.addPaper(wallpaper.bitmap)
            }
            aManager!!.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), timer, pi) }
    }

    private fun openSystemImageChooser(requestCode: Int) {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, requestCode, data)

        if (resultCode != RESULT_OK) {
            return
        }
        when (requestCode) {
            Companion.REQUEST_CODE_PICK ->{
                val uri = data?.data ?: return

                var filePath = uriPathHelper.getPath(this, uri).toString()
                val cut: Int = filePath.lastIndexOf('/')
                if (cut != -1) {
                    filePath= filePath.substring(cut + 1)
                }

                var imageInputStream: InputStream? = null
                try {
                    imageInputStream = contentResolver.openInputStream(uri)
                    val bitmap = BitmapFactory.decodeStream(imageInputStream)
                    val byte = BitmaptoByte(bitmap)
                    if (isExist(wallpaperList, bitmap)) {
                        Snackbar.make(binding.coordinatorlayout, "Image already exists!", Snackbar.LENGTH_SHORT).show()
                    }
                    else {
                        val values = ContentValues().apply {
                            put("filename", filePath)
                            put("image", byte)
                        }
                        val db = dbHelper.writableDatabase
                        db.insert("Wallpaper", null, values)
                        wallpaperList.add(Wallpaper(filePath, bitmap))
                        wallpaperAdapter()
                        addBinder.addPaper(bitmap)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    closeStream(imageInputStream)
                }
            }
            1 -> {
                timer = (data?.getLongExtra("return_timer", 60*1000)!!)*60000
                Log.d("timer", timer.toString())
                val timerSaver = getSharedPreferences("data", Context.MODE_PRIVATE).edit()
                timerSaver.putLong("timer", timer)
                timerSaver.apply()
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_PICK = 1000
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.addButton -> openSystemImageChooser(REQUEST_CODE_PICK)
            R.id.timerControl -> {
                val intent = Intent(this, TimerControler::class.java)
                intent.putExtra("timer", timer/60000)
                startActivityForResult(intent, 1)
            }
        }
        return true
    }

    private fun wallpaperAdapter(){
        val adapter = WallpaperAdapter(this, wallpaperList)
        binding.recyclerView.adapter = adapter
    }

    fun isExist(list:ArrayList<Wallpaper>, bitmap: Bitmap): Boolean {
        var temp = false
        for (i in list) {
            if (i.bitmap.sameAs(bitmap)){
                temp = true
                break
            }
        }
        return temp
    }

    fun BitmaptoByte(bitmap: Bitmap) : ByteArray{
        val  outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream)
        val byte = outputStream.toByteArray()
        return byte
    }
}

private fun closeStream(c: Closeable?) {
    try {
        c?.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }

}

