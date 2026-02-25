package com.example.wallpaperapp.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import coil.imageLoader
import com.example.wallpaperapp.R
import com.example.wallpaperapp.data.local.AuthStore
import com.example.wallpaperapp.data.local.FavoritesStore
import com.example.wallpaperapp.data.local.SettingsStore
import com.example.wallpaperapp.data.local.UserDataStore
import com.example.wallpaperapp.ui.auth.LoginActivity
import com.example.wallpaperapp.ui.home.HomeFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import androidx.appcompat.app.AppCompatDelegate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private lateinit var switchTheme: MaterialSwitch
    private lateinit var switchSaveGallery: MaterialSwitch
    private lateinit var switchAutoDownload: MaterialSwitch
    private lateinit var switchNsfw: MaterialSwitch
    private lateinit var switchSafeSearch: MaterialSwitch
    private lateinit var switchBlurPreview: MaterialSwitch

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnLoginLogout = view.findViewById<MaterialButton>(R.id.btnLoginLogout)
        val btnAccountLogin = view.findViewById<MaterialButton>(R.id.btnAccountLogin)
        val btnAccountLogout = view.findViewById<MaterialButton>(R.id.btnAccountLogout)
        val btnDeleteLocal = view.findViewById<MaterialButton>(R.id.btnDeleteLocal)
        val btnClearCache = view.findViewById<MaterialButton>(R.id.btnClearCache)
        val btnResetPrefs = view.findViewById<MaterialButton>(R.id.btnResetPrefs)

        switchTheme = view.findViewById(R.id.switchTheme)
        switchSaveGallery = view.findViewById(R.id.switchSaveGallery)
        switchAutoDownload = view.findViewById(R.id.switchAutoDownload)
        switchNsfw = view.findViewById(R.id.switchNsfw)
        switchSafeSearch = view.findViewById(R.id.switchSafeSearch)
        switchBlurPreview = view.findViewById(R.id.switchBlurPreview)

        initSwitches()

        switchTheme.setOnCheckedChangeListener { _, isChecked ->
            SettingsStore.setDarkMode(requireContext(), isChecked)
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

        switchSaveGallery.setOnCheckedChangeListener { _, isChecked ->
            SettingsStore.setSaveToGallery(requireContext(), isChecked)
        }

        switchAutoDownload.setOnCheckedChangeListener { _, isChecked ->
            SettingsStore.setAutoDownload(requireContext(), isChecked)
        }

        switchNsfw.setOnCheckedChangeListener { _, isChecked ->
            SettingsStore.setNsfwEnabled(requireContext(), isChecked)
        }

        switchSafeSearch.setOnCheckedChangeListener { _, isChecked ->
            SettingsStore.setSafeSearch(requireContext(), isChecked)
        }

        switchBlurPreview.setOnCheckedChangeListener { _, isChecked ->
            SettingsStore.setBlurPreview(requireContext(), isChecked)
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
            deleteLocalData()
        }

        btnClearCache.setOnClickListener {
            clearCache()
        }

        btnResetPrefs.setOnClickListener {
            showResetDialog()
        }
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
            val switchSafeSearch = v.findViewById<MaterialSwitch>(R.id.switchSafeSearch)
            val switchBlurPreview = v.findViewById<MaterialSwitch>(R.id.switchBlurPreview)

            likedCount.text = UserDataStore.getUserData(requireContext()).likedImages.size.toString()
            downloadsCount.text = FavoritesStore.getTotalDownloads(requireContext()).toString()
            collectionsCount.text = UserDataStore.getCollections(requireContext()).size.toString()

            switchTheme.isChecked = SettingsStore.isDarkMode(requireContext())
            switchSaveGallery.isChecked = SettingsStore.isSaveToGalleryEnabled(requireContext())
            switchAutoDownload.isChecked = SettingsStore.isAutoDownloadEnabled(requireContext())
            switchNsfw.isChecked = SettingsStore.isNsfwEnabled(requireContext())
            switchSafeSearch.isChecked = SettingsStore.isSafeSearchEnabled(requireContext())
            switchBlurPreview.isChecked = SettingsStore.isBlurPreviewEnabled(requireContext())

            val loggedIn = AuthStore.isLoggedIn(requireContext())
            if (loggedIn) {
                nameText.text = AuthStore.getLoggedInUser(requireContext()) ?: "Guest User"
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

    private fun initSwitches() {
        switchTheme.isChecked = SettingsStore.isDarkMode(requireContext())
        switchSaveGallery.isChecked = SettingsStore.isSaveToGalleryEnabled(requireContext())
        switchAutoDownload.isChecked = SettingsStore.isAutoDownloadEnabled(requireContext())
        switchNsfw.isChecked = SettingsStore.isNsfwEnabled(requireContext())
        switchSafeSearch.isChecked = SettingsStore.isSafeSearchEnabled(requireContext())
        switchBlurPreview.isChecked = SettingsStore.isBlurPreviewEnabled(requireContext())
    }

    private fun showResetDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Reset Preferences")
            .setMessage("This will reset all settings to default.")
            .setPositiveButton("Reset") { d, _ ->
                SettingsStore.resetAll(requireContext())
                initSwitches()
                Toast.makeText(requireContext(), "Preferences reset", Toast.LENGTH_SHORT).show()
                d.dismiss()
            }
            .setNegativeButton("Cancel") { d, _ ->
                d.dismiss()
            }
            .show()
    }

    private fun clearCache() {
        val ctx = requireContext()
        CoroutineScope(Dispatchers.IO).launch {
            ctx.imageLoader.memoryCache?.clear()
            ctx.imageLoader.diskCache?.clear()
            ctx.cacheDir?.deleteRecursively()
            ctx.cacheDir?.mkdirs()
            launch(Dispatchers.Main) {
                Toast.makeText(ctx, "Cache cleared", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteLocalData() {
        UserDataStore.clearAll(requireContext())
        SettingsStore.resetAll(requireContext())
        AuthStore.logout(requireContext())

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, HomeFragment())
            .commit()

        Toast.makeText(requireContext(), "Local data deleted", Toast.LENGTH_SHORT).show()
    }
}
