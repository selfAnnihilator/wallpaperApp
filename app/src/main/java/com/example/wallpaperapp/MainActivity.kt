package com.example.wallpaperapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.wallpaperapp.ui.home.HomeFragment
import com.example.wallpaperapp.ui.collections.CollectionsFragment
import com.example.wallpaperapp.ui.profile.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat


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
}