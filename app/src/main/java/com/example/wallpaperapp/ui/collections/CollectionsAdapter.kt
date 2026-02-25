package com.example.wallpaperapp.ui.collections

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.wallpaperapp.R
import com.example.wallpaperapp.data.local.UserDataStore

class CollectionsAdapter(
    private val context: Context,
    private val likedImages: List<String>,
    private val collections: List<String>,
    private val onClickLiked: () -> Unit,
    private val onClickCollection: (String) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_LIKED = 0
        const val TYPE_COLLECTION = 1
    }

    class LikedSectionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.likedTitle)
        val subtitle: TextView = view.findViewById(R.id.likedSubtitle)
        val previewList: RecyclerView = view.findViewById(R.id.likedPreviewList)
    }

    class CollectionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val preview: ImageView = view.findViewById(R.id.collectionPreview)
        val title: TextView = view.findViewById(R.id.collectionTitle)
    }

    override fun getItemCount(): Int = 1 + collections.size

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) TYPE_LIKED else TYPE_COLLECTION
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_LIKED) {
            val view = inflater.inflate(R.layout.item_liked_section, parent, false)
            LikedSectionViewHolder(view)
        } else {
            val view = inflater.inflate(R.layout.item_collection_card, parent, false)
            CollectionViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is LikedSectionViewHolder) {
            holder.title.text = "Liked"
            holder.subtitle.text = "${likedImages.size} liked wallpapers"

            holder.previewList.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            holder.previewList.adapter = LikedPreviewAdapter(likedImages.take(4))

            holder.itemView.setOnClickListener { onClickLiked() }
        } else if (holder is CollectionViewHolder) {
            val name = collections[position - 1]
            holder.title.text = name

            val imgs = UserDataStore.getCollectionImages(context, name)
            if (imgs.isNotEmpty()) {
                holder.preview.load(imgs.first()) {
                    crossfade(true)
                    placeholder(R.drawable.placeholder)
                    error(R.drawable.placeholder)
                }
            } else {
                holder.preview.setImageResource(R.drawable.placeholder)
            }

            holder.itemView.setOnClickListener { onClickCollection(name) }
        }
    }
}
