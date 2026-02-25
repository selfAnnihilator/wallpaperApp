package com.example.wallpaperapp.ui.auth

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wallpaperapp.R
import com.example.wallpaperapp.data.local.AuthStore
import com.example.wallpaperapp.data.local.UserStore
import com.google.android.material.button.MaterialButton

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val usernameInput = findViewById<EditText>(R.id.inputUsername)
        val passwordInput = findViewById<EditText>(R.id.inputPassword)
        val confirmInput = findViewById<EditText>(R.id.inputConfirmPassword)
        val btnRegister = findViewById<MaterialButton>(R.id.btnRegister)
        val btnBackToLogin = findViewById<MaterialButton>(R.id.btnBackToLogin)

        btnRegister.setOnClickListener {
            val username = usernameInput.text.toString().trim()
            val password = passwordInput.text.toString()
            val confirm = confirmInput.text.toString()

            if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirm) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val registered = UserStore.registerUser(this, username, password)
            if (registered) {
                AuthStore.login(this, username)
                finish()
            } else {
                Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show()
            }
        }

        btnBackToLogin.setOnClickListener {
            finish()
        }
    }
}
