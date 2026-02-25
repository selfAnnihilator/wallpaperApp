package com.example.wallpaperapp.ui.auth

import android.os.Bundle
import android.widget.Toast
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.wallpaperapp.R
import com.example.wallpaperapp.data.local.AuthStore
import com.example.wallpaperapp.data.local.UserStore
import com.google.android.material.button.MaterialButton

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val usernameInput = findViewById<EditText>(R.id.inputUsername)
        val passwordInput = findViewById<EditText>(R.id.inputPassword)
        val btnLogin = findViewById<MaterialButton>(R.id.btnLogin)
        val btnGuest = findViewById<MaterialButton>(R.id.btnGuest)
        val btnCreateAccount = findViewById<MaterialButton>(R.id.btnCreateAccount)

        btnLogin.setOnClickListener {
            val username = usernameInput.text.toString().trim()
            val password = passwordInput.text.toString()
            val isValid = UserStore.validateUser(this, username, password)
            if (isValid) {
                AuthStore.login(this, username)
                finish()
            } else {
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show()
            }
        }

        btnGuest.setOnClickListener {
            finish()
        }

        btnCreateAccount.setOnClickListener {
            startActivity(android.content.Intent(this, RegisterActivity::class.java))
        }
    }
}
