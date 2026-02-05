package com.example.wallpaperapp.ui.collections

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.wallpaperapp.R
import com.example.wallpaperapp.data.local.CollectionsStore

class CollectionsAdapter(
    private val context: Context,
    private val collections: List<String>
) : RecyclerView.Adapter<CollectionsAdapter.VH>() {

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val preview: ImageView = view.findViewById(R.id.collectionPreview)
        val title: TextView = view.findViewById(R.id.collectionTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_collection, parent, false)
        return VH(view)
    }

    override fun getItemCount(): Int = collections.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val name = collections[position]
        holder.title.text = name

        val imgs = CollectionsStore.getImages(context, name)

        if (imgs.isNotEmpty()) {
            holder.preview.load(imgs.first())
        } else {
            holder.preview.setImageResource(R.drawable.placeholder)
        }
    }
}
