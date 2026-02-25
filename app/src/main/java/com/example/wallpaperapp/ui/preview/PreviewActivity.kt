package com.example.wallpaperapp.ui.preview

import android.content.ContentValues
import android.os.Bundle
import android.os.Environment
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
import com.example.wallpaperapp.data.local.UserDataStore
import com.example.wallpaperapp.data.local.SettingsStore
import androidx.fragment.app.Fragment
import com.example.wallpaperapp.ui.auth.AuthUtils
import java.io.File

class PreviewActivity : AppCompatActivity() {

    private lateinit var imageUrl: String
    private lateinit var id: String
    private lateinit var authFragment: Fragment
    private lateinit var btnSave: ShapeableImageView

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
        btnSave = findViewById(R.id.btnSave)

        authFragment = supportFragmentManager.findFragmentByTag("auth_host")
            ?: Fragment()
        if (!authFragment.isAdded) {
            supportFragmentManager.beginTransaction()
                .add(authFragment, "auth_host")
                .commitNow()
        }

        imageUrl = intent.getStringExtra("imageUrl") ?: return
        id = imageUrl

        imageView.load(imageUrl)

        // --------------------
        // restore states
        // --------------------

        btnLike.setImageResource(
            if (UserDataStore.isLiked(this, id))
                R.drawable.ic_favorite
            else
                R.drawable.ic_favorite_outline
        )

        btnSave.setImageResource(
            if (UserDataStore.isInAnyCollection(this, id))
                R.drawable.ic_bookmark
            else
                R.drawable.ic_bookmark_outline
        )

        // --------------------
        // ❤️ FAVORITE (heart)
        // --------------------

        btnLike.setOnClickListener {
            AuthUtils.requireLogin(authFragment) {
                UserDataStore.toggleLike(this, id)
                val fav = UserDataStore.isLiked(this, id)

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
        }

        // --------------------
        // 🔖 SAVE (bookmark -> add to collection)
        // --------------------

        btnSave.setOnClickListener {
            AuthUtils.requireLogin(authFragment) {
                showCollectionPicker()
            }
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

                if (SettingsStore.isSaveToGalleryEnabled(this@PreviewActivity)) {
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
                } else {
                    val dir = File(
                        getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                        "Wallpapers"
                    )
                    if (!dir.exists()) {
                        dir.mkdirs()
                    }
                    val file = File(dir, filename)
                    file.outputStream().use { output ->
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
        val names = UserDataStore.getCollections(this)

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
                UserDataStore.addToCollection(this, names[which], id)

                btnSave.setImageResource(R.drawable.ic_bookmark)

                Toast.makeText(
                    this,
                    "Saved to ${names[which]}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .show()
    }

}
