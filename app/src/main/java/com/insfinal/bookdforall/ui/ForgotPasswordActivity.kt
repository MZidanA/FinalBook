package com.insfinal.bookdforall.ui

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope // Pastikan ini diimpor jika Anda tetap menggunakannya untuk skenario lain
import com.insfinal.bookdforall.databinding.ActivityForgotPasswordBinding
import com.insfinal.bookdforall.repository.AuthRepository // Pastikan ini diimpor jika Anda tetap menggunakannya untuk skenario lain
import kotlinx.coroutines.launch // Pastikan ini diimpor jika Anda tetap menggunakannya untuk skenario lain

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding

    // Email statis yang akan dianggap "valid" atau "terdaftar" untuk tujuan pengujian
    // Jika email yang dimasukkan pengguna sama dengan ini, akan lanjut ke halaman SetNewPasswordActivity
    private val PREDEFINED_VALID_EMAIL = "user@example.com" // <--- Ganti dengan email yang Anda inginkan untuk pengujian

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSendResetLink.setOnClickListener {
            val enteredEmail = binding.editTextEmail.text.toString().trim() // Ambil email dari input pengguna

            // 1. Validasi Format Email
            if (!Patterns.EMAIL_ADDRESS.matcher(enteredEmail).matches()) {
                Toast.makeText(this, "Format email tidak valid.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 2. Cek Apakah Email yang Dimasukkan Sesuai dengan Email Statis
            if (enteredEmail == PREDEFINED_VALID_EMAIL) {
                // Email cocok dengan yang sudah ditentukan, anggap seperti API mengembalikan sukses
                Toast.makeText(this@ForgotPasswordActivity, "Silakan atur password baru Anda.", Toast.LENGTH_LONG).show()

                val intent = Intent(this@ForgotPasswordActivity, SetNewPasswordActivity::class.java)
                intent.putExtra("email", enteredEmail) // Kirim email yang dimasukkan pengguna
                startActivity(intent)
                finish()
            } else {
                // Email tidak cocok dengan yang sudah ditentukan, anggap seperti API mengembalikan "tidak ditemukan"
                Toast.makeText(this@ForgotPasswordActivity, "Email tidak ditemukan atau tidak terdaftar.", Toast.LENGTH_SHORT).show()
            }

            // Bagian di bawah ini adalah kode asli Anda untuk memanggil AuthRepository().forgotPassword(email).
            // Anda bisa mengkomentari atau menghapusnya jika Anda hanya ingin menguji dengan validasi lokal ini.
            // Jika Anda ingin tetap menggunakannya untuk memanggil API setelah validasi lokal gagal,
            // Anda bisa menyesuaikan logikanya. Untuk tujuan pengujian yang Anda minta,
            // kita akan fokus pada validasi email lokal di atas.
            /*
            lifecycleScope.launch {
                try {
                    val response = AuthRepository().forgotPassword(enteredEmail)
                    if (response.isSuccessful) {
                        Toast.makeText(this@ForgotPasswordActivity, "Silakan atur password baru Anda.", Toast.LENGTH_LONG).show()

                        val intent = Intent(this@ForgotPasswordActivity, SetNewPasswordActivity::class.java)
                        intent.putExtra("email", enteredEmail)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@ForgotPasswordActivity, "Email tidak ditemukan.", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@ForgotPasswordActivity, "Terjadi kesalahan: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
            */
        }
        binding.btnKembali.setOnClickListener {
            finish()
        }
    }
}