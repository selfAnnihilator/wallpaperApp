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

class WallpaperAdapter(
    private val wallpapers: MutableList<WallhavenWallpaper>,
    private val onClick: (WallhavenWallpaper) -> Unit
) : RecyclerView.Adapter<WallpaperAdapter.WallpaperViewHolder>() {

    inner class WallpaperViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.wallpaperImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WallpaperViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_wallpaper, parent, false)
        return WallpaperViewHolder(view)
    }

    override fun onBindViewHolder(holder: WallpaperViewHolder, position: Int) {
        val wallpaper = wallpapers[position]

        // ✅ Load thumbnail from URL
        holder.image.load(wallpaper.thumbs.small) {
            crossfade(true)
            placeholder(R.drawable.placeholder)
            error(R.drawable.placeholder)
        }

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, PreviewActivity::class.java)

            // ✅ Pass full image URL to preview
            intent.putExtra("imageUrl", wallpaper.path)

            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = wallpapers.size
}
