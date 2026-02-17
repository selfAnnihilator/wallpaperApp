package com.example.wallpaperapp.ui.collections

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.wallpaperapp.R
import com.example.wallpaperapp.ui.preview.PreviewActivity

class CollectionDetailAdapter(
    private val images: List<String>
) : RecyclerView.Adapter<CollectionDetailAdapter.VH>() {

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.wallpaperImage)
        val title: TextView = view.findViewById(R.id.wallpaperTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_collection_detail, parent, false)
        return VH(view)
    }

    override fun getItemCount(): Int = images.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val url = images[position]

        holder.image.load(url) {
            crossfade(true)
            placeholder(R.drawable.placeholder)
            error(R.drawable.placeholder)
        }

        holder.title.text = url.takeLast(8)

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, PreviewActivity::class.java)
            intent.putExtra("imageUrl", url)
            context.startActivity(intent)
        }
    }
}
