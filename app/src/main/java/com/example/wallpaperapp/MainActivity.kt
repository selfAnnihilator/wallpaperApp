package com.example.wallpaperapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsCompat
import com.example.wallpaperapp.ui.home.HomeFragment
import com.example.wallpaperapp.ui.collections.CollectionsFragment
import com.example.wallpaperapp.ui.profile.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.core.view.WindowInsetsControllerCompat
import com.example.wallpaperapp.data.local.AuthStore
import com.example.wallpaperapp.ui.auth.LoginActivity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.statusBars())
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, HomeFragment())
            .commit()

        bottomNav.setOnItemSelectedListener { menuItem ->
            if (menuItem.itemId == R.id.nav_collections && !AuthStore.isLoggedIn(this)) {
                showLoginRequiredDialog()
                return@setOnItemSelectedListener false
            }

            val fragment = when (menuItem.itemId) {
                R.id.nav_home -> HomeFragment()
                R.id.nav_collections -> CollectionsFragment()
                R.id.nav_profile -> ProfileFragment()
                else -> HomeFragment()
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit()

            true
        }
    }

    private fun showLoginRequiredDialog() {
        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle("Login Required")
            .setMessage("Create an account to access collections.")
            .setPositiveButton("Login") { d, _ ->
                startActivity(Intent(this, LoginActivity::class.java))
                d.dismiss()
            }
            .setNegativeButton("Cancel") { d, _ ->
                d.dismiss()
            }
            .create()

        dialog.setOnShowListener {
            val radius = resources.displayMetrics.density * 18f
            val bg = GradientDrawable().apply {
                setColor(Color.BLACK)
                cornerRadius = radius
            }
            dialog.window?.setBackgroundDrawable(bg)
            dialog.findViewById<TextView>(android.R.id.message)?.setTextColor(Color.WHITE)
            dialog.findViewById<TextView>(com.google.android.material.R.id.alertTitle)
                ?.setTextColor(Color.WHITE)
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(Color.WHITE)
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(Color.LTGRAY)
        }

        dialog.show()
    }
}
