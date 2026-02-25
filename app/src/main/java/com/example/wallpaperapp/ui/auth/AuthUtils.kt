package com.example.wallpaperapp.ui.auth

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.wallpaperapp.data.local.AuthStore
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object AuthUtils {
    fun requireLogin(
        fragment: Fragment,
        onAuthenticated: () -> Unit
    ) {
        if (AuthStore.isLoggedIn(fragment.requireContext())) {
            onAuthenticated()
            return
        }

        val context = fragment.requireContext()

        val dialog = MaterialAlertDialogBuilder(context)
            .setTitle("Login Required")
            .setMessage("You need an account to like images.")
            .setPositiveButton("Login") { d, _ ->
                fragment.startActivity(Intent(context, LoginActivity::class.java))
                d.dismiss()
            }
            .setNegativeButton("Cancel") { d, _ ->
                d.dismiss()
            }
            .create()

        dialog.setOnShowListener {
            val radius = context.resources.displayMetrics.density * 18f
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
