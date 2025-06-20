// RegisterActivity.kt
package com.insfinal.bookdforall.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.insfinal.bookdforall.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener {
            val name = binding.etNama.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Semua kolom harus diisi", Toast.LENGTH_SHORT).show()
            } else {
                // --- LOGIKA PENDAFTARAN SEBENARNYA AKAN DITEMPATKAN DI SINI ---
                // Saat ini, ini hanya simulasi keberhasilan.
                // Dalam aplikasi nyata:
                // 1. Panggil API backend untuk mendaftarkan pengguna baru.
                // 2. Tangani respons dari server (berhasil/gagal).
                // 3. Jika berhasil, mungkin simpan token sesi atau status login.

                Toast.makeText(this, "Pendaftaran berhasil untuk: $email", Toast.LENGTH_LONG).show()

                // Contoh: Setelah pendaftaran berhasil, arahkan ke LoginActivity
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish() // Tutup RegisterActivity
            }
        }

        binding.tvLoginLink.setOnClickListener {
            // Arahkan kembali ke LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}