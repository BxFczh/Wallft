package com.hzzh.wallft

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar

class WallpaperAdapter (val context: Context, val wallpaperList: ArrayList<Wallpaper>) : RecyclerView.Adapter<WallpaperAdapter.ViewHolder>() {

//    private lateinit var binding: ActivityMainBinding

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val wallpaperImage: ImageView = view.findViewById(R.id.wallpaperImage)
        val wallpaperName: TextView = view.findViewById(R.id.wallpaperName)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WallpaperAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.wallpaper_item, parent, false)
        val viewHolder = ViewHolder(view)
        val dbHelper = WallpapersDatabase(parent.context, "Wallpapers.db", 1)
//        binding = ActivityMainBinding.inflate(LayoutInflater.from(MainActivity))
        viewHolder.wallpaperImage.setOnLongClickListener {
            val position = viewHolder.adapterPosition
            val db = dbHelper.writableDatabase
            db.delete("Wallpaper", "filename = ?", arrayOf(wallpaperList[position].name))
            val deletedWallpaper = wallpaperList[position]
            wallpaperList.removeAt(position)

            notifyItemRemoved(position)
//            notifyItemRangeChanged(position,wallpaperList.size-position)
            Snackbar.make((context as Activity).findViewById(android.R.id.content), "Image already deleted.", Snackbar.LENGTH_SHORT)
                .setAction("Undo") {
                    wallpaperList.add(position,deletedWallpaper)
                    notifyItemInserted(position)
//                            notifyItemRangeChanged(position,wallpaperList.size + position)
                }.show()
            true
        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: WallpaperAdapter.ViewHolder, position: Int) {
        val wallpaper = wallpaperList[position]
//        holder.wallpaperImage.setImageBitmap(wallpaper.bitmap)
        holder.wallpaperName.text = wallpaper.name
        Glide.with(context).load(wallpaper.bitmap).into(holder.wallpaperImage)
    }

    override fun getItemCount() = wallpaperList.size
}

