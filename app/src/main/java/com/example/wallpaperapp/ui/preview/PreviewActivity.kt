package com.example.wallpaperapp.ui.preview

import android.content.ContentValues
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.example.wallpaperapp.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import kotlinx.coroutines.*
import java.net.URL
import com.example.wallpaperapp.data.local.FavoritesStore
import androidx.appcompat.app.AlertDialog
import com.example.wallpaperapp.data.local.CollectionsStore

class PreviewActivity : AppCompatActivity() {

    private lateinit var imageUrl: String
    private lateinit var id: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview)

        // fullscreen
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

        val imageView = findViewById<ImageView>(R.id.previewImage)
        val btnDownload = findViewById<MaterialButton>(R.id.btnDownload)
        val btnLike = findViewById<ShapeableImageView>(R.id.btnLike)
        val btnSave = findViewById<ShapeableImageView>(R.id.btnSave)

        imageUrl = intent.getStringExtra("imageUrl") ?: return
        id = imageUrl

        imageView.load(imageUrl)

        // --------------------
        // restore states
        // --------------------

        btnLike.setImageResource(
            if (FavoritesStore.isLiked(this, id))
                R.drawable.ic_favorite
            else
                R.drawable.ic_favorite_outline
        )

        btnSave.setImageResource(
            if (FavoritesStore.isSaved(this, id))
                R.drawable.ic_bookmark
            else
                R.drawable.ic_bookmark_outline
        )

        // --------------------
        // ❤️ FAVORITE (heart)
        // --------------------

        btnLike.setOnClickListener {

            val fav = FavoritesStore.toggleLike(this, id)

            btnLike.setImageResource(
                if (fav) R.drawable.ic_favorite
                else R.drawable.ic_favorite_outline
            )

            Toast.makeText(
                this,
                if (fav) "Added to favorites"
                else "Removed from favorites",
                Toast.LENGTH_SHORT
            ).show()
        }

        // --------------------
        // 🔖 SAVE (bookmark -> add to collection)
        // --------------------

        btnSave.setOnClickListener {
            showCollectionPicker()
        }

        // --------------------
        // DOWNLOAD
        // --------------------

        btnDownload.setOnClickListener {
            FavoritesStore.addDownload(this, id)
            downloadAndSaveImage()
        }

    }

    private fun downloadAndSaveImage() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val inputStream = URL(imageUrl).openStream()
                val filename = "wallpaper_${System.currentTimeMillis()}.jpg"

                val values = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Wallpapers")
                }

                val uri = contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    values
                )

                uri?.let {
                    contentResolver.openOutputStream(it)?.use { output ->
                        inputStream.copyTo(output)
                    }
                }

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@PreviewActivity, "Saved", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@PreviewActivity, "Download failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showCollectionPicker() {
        val names = CollectionsStore.getCollections(this)

        if (names.isEmpty()) {
            Toast.makeText(
                this,
                "Create a collection first",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        AlertDialog.Builder(this)
            .setTitle("Save to collection")
            .setItems(names.toTypedArray()) { _, which ->
                CollectionsStore.addToCollection(
                    this,
                    names[which],
                    id
                )

                Toast.makeText(
                    this,
                    "Saved to ${names[which]}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .show()
    }

}
