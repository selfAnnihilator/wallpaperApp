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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

class PreviewActivity : AppCompatActivity() {

    private lateinit var imageUrl: String
    private var liked = false
    private var saved = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview)

        // Fullscreen
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

        val imageView = findViewById<ImageView>(R.id.previewImage)
        val downloadButton = findViewById<MaterialButton>(R.id.downloadButton)
        val btnLike = findViewById<ShapeableImageView>(R.id.btnLike)
        val btnSave = findViewById<ShapeableImageView>(R.id.btnSave)

        imageUrl = intent.getStringExtra("imageUrl") ?: return

        // ✅ Load full wallpaper
        imageView.load(imageUrl) {
            crossfade(true)
        }

        // ✅ Download button
        downloadButton.setOnClickListener {
            downloadAndSaveImage()
        }

        btnLike.setOnClickListener {

            liked = !liked

            btnLike.setImageResource(
                if (liked) R.drawable.ic_favorite
                else R.drawable.ic_favorite_outline
            )
        }

        btnSave.setOnClickListener {

            saved = !saved

            btnSave.setImageResource(
                if (saved) R.drawable.ic_bookmark
                else R.drawable.ic_bookmark_outline
            )
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

                val resolver = contentResolver
                val uri = resolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    values
                )

                uri?.let {
                    resolver.openOutputStream(it)?.use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@PreviewActivity,
                        "Saved to Gallery",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@PreviewActivity,
                        "Download failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}
