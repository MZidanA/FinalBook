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
                // --- LOGIKA LOGIN SEBENARNYA AKAN DITEMPATKAN DI SINI ---
                // Saat ini, ini hanya simulasi keberhasilan.
                // Dalam aplikasi nyata:
                // 1. Panggil API backend untuk memverifikasi kredensial.
                // 2. Tangani respons dari server (berhasil/gagal).
                // 3. Jika berhasil, simpan status login (misal, Shared Preferences)
                //    dan token autentikasi (jika ada).

                // SIMULASI LOGIN BERHASIL:
                // Misalnya, jika email adalah "test@example.com" dan password "password123"
                if (email == "test@example.com" && password == "password123") {
                    Toast.makeText(this, "Login berhasil!", Toast.LENGTH_SHORT).show()

                    // Arahkan ke MainActivity setelah login berhasil
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish() // Tutup LoginActivity agar pengguna tidak bisa kembali dengan tombol back
                } else {
                    Toast.makeText(this, "Email atau password salah.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.textViewForgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        binding.textViewRegister.setOnClickListener {
            // Arahkan ke RegisterActivity
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}