package com.example.wallpaperapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.wallpaperapp.R
import com.example.wallpaperapp.data.local.AuthStore
import com.google.android.material.button.MaterialButton

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val usernameInput = findViewById<EditText>(R.id.inputUsername)
        val passwordInput = findViewById<EditText>(R.id.inputPassword)
        val btnLogin = findViewById<MaterialButton>(R.id.btnLogin)
        val btnGuest = findViewById<MaterialButton>(R.id.btnGuest)

        btnLogin.setOnClickListener {
            val username = usernameInput.text.toString().trim()
            if (username.isNotEmpty()) {
                AuthStore.login(this, username)
                goToMain()
            }
        }

        btnGuest.setOnClickListener {
            AuthStore.logout(this)
            goToMain()
        }
    }

    private fun goToMain() {
        val intent = Intent(this, com.example.wallpaperapp.MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}
