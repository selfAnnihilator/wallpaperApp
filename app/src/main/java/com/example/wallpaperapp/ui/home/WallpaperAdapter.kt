package com.example.wallpaperapp.ui.home

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.wallpaperapp.R
import com.example.wallpaperapp.data.wallhaven.WallhavenWallpaper
import com.example.wallpaperapp.ui.preview.PreviewActivity
import android.widget.TextView

class WallpaperAdapter(
    private val wallpapers: MutableList<WallhavenWallpaper>,
    private val onClick: (WallhavenWallpaper) -> Unit
) : RecyclerView.Adapter<WallpaperAdapter.WallpaperViewHolder>() {

    // ✅ in-memory state (no DB yet)
    private val likedIds: MutableSet<String> = mutableSetOf()
    private val savedIds: MutableSet<String> = mutableSetOf()

    inner class WallpaperViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.wallpaperImage)
        val title: TextView = itemView.findViewById(R.id.wallpaperTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WallpaperViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_wallpaper, parent, false)
        return WallpaperViewHolder(view)
    }

    override fun onBindViewHolder(holder: WallpaperViewHolder, position: Int) {
        val wallpaper = wallpapers[position]

        // ✅ load image
        holder.image.load(wallpaper.thumbs.small) {
            crossfade(true)
            placeholder(R.drawable.placeholder)
            error(R.drawable.placeholder)
        }

        holder.title.text = wallpaper.id.take(8)

        // ✅ open preview
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, PreviewActivity::class.java)
            intent.putExtra("imageUrl", wallpaper.path)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = wallpapers.size
}
