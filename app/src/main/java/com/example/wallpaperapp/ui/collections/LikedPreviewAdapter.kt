package com.example.wallpaperapp.ui.collections

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.wallpaperapp.R

class LikedPreviewAdapter(
    private val images: List<String>
) : RecyclerView.Adapter<LikedPreviewAdapter.VH>() {

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.previewImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_liked_preview, parent, false)
        return VH(view)
    }

    override fun getItemCount(): Int = images.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.image.load(images[position]) {
            crossfade(true)
            placeholder(R.drawable.placeholder)
            error(R.drawable.placeholder)
        }
    }
}
