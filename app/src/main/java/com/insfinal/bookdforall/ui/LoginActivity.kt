package com.insfinal.bookdforall.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.booksforall.R
import com.example.booksforall.databinding.ActivityLoginBinding
import com.insfinal.bookdforall.ui.MainActivity // Pastikan Anda memiliki MainActivity
import com.insfinal.bookdforall.utils.tintStartIcon

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonLogin.setOnClickListener {
            val email = binding.editTextEmailLogin.text.toString().trim()
            tintStartIcon(binding.textInputLayoutEmail, R.color.icon_color, this)
            val password = binding.editTextPasswordLogin.text.toString().trim()
            tintStartIcon(binding.textInputPasswordLogin, R.color.icon_color, this)


            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan password tidak boleh kosong", Toast.LENGTH_SHORT).show()
            } else {
                if (email == "test@example.com" && password == "password123") {
                    Toast.makeText(this, "Login berhasil!", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Email atau password salah.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.textViewForgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        binding.textViewRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}