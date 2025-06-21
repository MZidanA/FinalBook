// ui/ChangePasswordActivity.kt
package com.insfinal.bookdforall.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.insfinal.bookdforall.databinding.ActivityChangePasswordBinding
import com.insfinal.bookdforall.network.RetrofitInstance // Use RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangePasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnKembali.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.buttonVerifyPassword.setOnClickListener {
            val oldPassword = binding.editTextOldPassword.text.toString().trim()
            val sharedPrefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            val userId = sharedPrefs.getInt("user_id", -1)

            if (oldPassword.isEmpty()) {
                Toast.makeText(this, "Kata sandi lama tidak boleh kosong", Toast.LENGTH_SHORT).show()
            } else if (userId == -1) {
                Toast.makeText(this, "User ID tidak ditemukan. Mohon login ulang.", Toast.LENGTH_SHORT).show()
            } else {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val response = RetrofitInstance.api.getUser(userId) // Menggunakan RetrofitInstance.api
                        withContext(Dispatchers.Main) {
                            if (response.isSuccessful) {
                                val user = response.body()
                                // PENTING: Ini mengasumsikan bahwa endpoint GET /users/{id} mengembalikan password.
                                // Ini umumnya TIDAK direkomendasikan untuk alasan keamanan.
                                // Solusi yang lebih baik adalah endpoint backend untuk memverifikasi password lama secara langsung.
                                if (user != null && oldPassword == user.password) {
                                    val intent = Intent(this@ChangePasswordActivity, NewPasswordActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    Toast.makeText(this@ChangePasswordActivity, "Kata sandi lama salah", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                val errorBody = response.errorBody()?.string()
                                Toast.makeText(this@ChangePasswordActivity, "Gagal memverifikasi password: ${errorBody ?: response.message()}", Toast.LENGTH_LONG).show()
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@ChangePasswordActivity, "Error verifikasi password: ${e.message}", Toast.LENGTH_LONG).show()
                            e.printStackTrace()
                        }
                    }
                }
            }
        }

        binding.tvForgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
    }
}