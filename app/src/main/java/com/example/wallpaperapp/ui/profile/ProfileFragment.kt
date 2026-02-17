package com.example.wallpaperapp.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import coil.imageLoader
import com.example.wallpaperapp.R
import com.example.wallpaperapp.data.local.AuthStore
import com.example.wallpaperapp.data.local.CollectionsStore
import com.example.wallpaperapp.data.local.FavoritesStore
import com.example.wallpaperapp.data.local.SettingsStore
import com.example.wallpaperapp.ui.auth.LoginActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.materialswitch.MaterialSwitch

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val nameText = view.findViewById<TextView>(R.id.profileName)
        val subtitleText = view.findViewById<TextView>(R.id.profileSubtitle)
        val btnLoginLogout = view.findViewById<MaterialButton>(R.id.btnLoginLogout)
        val btnAccountLogin = view.findViewById<MaterialButton>(R.id.btnAccountLogin)
        val btnAccountLogout = view.findViewById<MaterialButton>(R.id.btnAccountLogout)
        val btnDeleteLocal = view.findViewById<MaterialButton>(R.id.btnDeleteLocal)

        val likedCount = view.findViewById<TextView>(R.id.statLikedCount)
        val downloadsCount = view.findViewById<TextView>(R.id.statDownloadsCount)
        val collectionsCount = view.findViewById<TextView>(R.id.statCollectionsCount)

        val switchTheme = view.findViewById<MaterialSwitch>(R.id.switchTheme)
        val switchSaveGallery = view.findViewById<MaterialSwitch>(R.id.switchSaveGallery)
        val switchAutoDownload = view.findViewById<MaterialSwitch>(R.id.switchAutoDownload)

        val switchNsfw = view.findViewById<MaterialSwitch>(R.id.switchNsfw)
        val switchSafeSearch = view.findViewById<MaterialSwitch>(R.id.switchSafeSearch)
        val switchBlurPreview = view.findViewById<MaterialSwitch>(R.id.switchBlurPreview)

        val btnClearCache = view.findViewById<MaterialButton>(R.id.btnClearCache)
        val btnResetPrefs = view.findViewById<MaterialButton>(R.id.btnResetPrefs)

        likedCount.text = FavoritesStore.getAllLiked(requireContext()).size.toString()
        downloadsCount.text = FavoritesStore.getTotalDownloads(requireContext()).toString()
        collectionsCount.text = CollectionsStore.getCollections(requireContext()).size.toString()

        switchTheme.isChecked = true
        switchTheme.isEnabled = false

        switchSaveGallery.isChecked = SettingsStore.getSaveToGallery(requireContext())
        switchAutoDownload.isChecked = SettingsStore.getAutoDownload(requireContext())
        switchNsfw.isChecked = SettingsStore.isNsfw(requireContext())

        switchSaveGallery.setOnCheckedChangeListener { _, isChecked ->
            SettingsStore.setSaveToGallery(requireContext(), isChecked)
        }

        switchAutoDownload.setOnCheckedChangeListener { _, isChecked ->
            SettingsStore.setAutoDownload(requireContext(), isChecked)
        }

        switchNsfw.setOnCheckedChangeListener { _, isChecked ->
            SettingsStore.setNsfw(requireContext(), isChecked)
        }

        val loggedIn = AuthStore.isLoggedIn(requireContext())
        if (loggedIn) {
            nameText.text = AuthStore.getUsername(requireContext())
            subtitleText.text = "Wallpaper Explorer"
            btnLoginLogout.text = "Logout"
            btnAccountLogin.visibility = View.GONE
            btnAccountLogout.visibility = View.VISIBLE
        } else {
            nameText.text = "Guest User"
            subtitleText.text = "Wallpaper Explorer"
            btnLoginLogout.text = "Login"
            btnAccountLogin.visibility = View.VISIBLE
            btnAccountLogout.visibility = View.GONE
        }

        btnLoginLogout.setOnClickListener {
            if (AuthStore.isLoggedIn(requireContext())) {
                AuthStore.logout(requireContext())
                refresh()
            } else {
                startActivity(Intent(requireContext(), LoginActivity::class.java))
            }
        }

        btnAccountLogin.setOnClickListener {
            startActivity(Intent(requireContext(), LoginActivity::class.java))
        }

        btnAccountLogout.setOnClickListener {
            AuthStore.logout(requireContext())
            refresh()
        }

        btnDeleteLocal.setOnClickListener {
            AuthStore.logout(requireContext())
            SettingsStore.setNsfw(requireContext(), false)
            SettingsStore.setSaveToGallery(requireContext(), false)
            SettingsStore.setAutoDownload(requireContext(), false)
            refresh()
        }

        btnClearCache.setOnClickListener {
            requireContext().imageLoader.memoryCache?.clear()
        }

        btnResetPrefs.setOnClickListener {
            SettingsStore.setNsfw(requireContext(), false)
            SettingsStore.setSaveToGallery(requireContext(), false)
            SettingsStore.setAutoDownload(requireContext(), false)
            refresh()
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    private fun refresh() {
        view?.let { v ->
            val nameText = v.findViewById<TextView>(R.id.profileName)
            val subtitleText = v.findViewById<TextView>(R.id.profileSubtitle)
            val btnLoginLogout = v.findViewById<MaterialButton>(R.id.btnLoginLogout)
            val btnAccountLogin = v.findViewById<MaterialButton>(R.id.btnAccountLogin)
            val btnAccountLogout = v.findViewById<MaterialButton>(R.id.btnAccountLogout)

            val likedCount = v.findViewById<TextView>(R.id.statLikedCount)
            val downloadsCount = v.findViewById<TextView>(R.id.statDownloadsCount)
            val collectionsCount = v.findViewById<TextView>(R.id.statCollectionsCount)

            val switchSaveGallery = v.findViewById<MaterialSwitch>(R.id.switchSaveGallery)
            val switchAutoDownload = v.findViewById<MaterialSwitch>(R.id.switchAutoDownload)
            val switchNsfw = v.findViewById<MaterialSwitch>(R.id.switchNsfw)

            likedCount.text = FavoritesStore.getAllLiked(requireContext()).size.toString()
            downloadsCount.text = FavoritesStore.getTotalDownloads(requireContext()).toString()
            collectionsCount.text = CollectionsStore.getCollections(requireContext()).size.toString()

            switchSaveGallery.isChecked = SettingsStore.getSaveToGallery(requireContext())
            switchAutoDownload.isChecked = SettingsStore.getAutoDownload(requireContext())
            switchNsfw.isChecked = SettingsStore.isNsfw(requireContext())

            val loggedIn = AuthStore.isLoggedIn(requireContext())
            if (loggedIn) {
                nameText.text = AuthStore.getUsername(requireContext())
                subtitleText.text = "Wallpaper Explorer"
                btnLoginLogout.text = "Logout"
                btnAccountLogin.visibility = View.GONE
                btnAccountLogout.visibility = View.VISIBLE
            } else {
                nameText.text = "Guest User"
                subtitleText.text = "Wallpaper Explorer"
                btnLoginLogout.text = "Login"
                btnAccountLogin.visibility = View.VISIBLE
                btnAccountLogout.visibility = View.GONE
            }
        }
    }
}
