// ui/NewPasswordActivity.kt
package com.insfinal.bookdforall.ui

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.insfinal.bookdforall.databinding.ActivityNewPasswordBinding
import com.insfinal.bookdforall.network.RetrofitInstance // Use RetrofitInstance
import com.insfinal.bookdforall.model.User // Import User model
import com.insfinal.bookdforall.model.CreateUserRequest // Import CreateUserRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NewPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnKembali.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.buttonSetNewPassword.setOnClickListener {
            val newPassword = binding.editTextNewPassword.text.toString().trim()
            val confirmPassword = binding.editTextConfirmNewPassword.text.toString().trim()

            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Kata sandi baru tidak boleh kosong", Toast.LENGTH_SHORT).show()
            } else if (newPassword != confirmPassword) {
                Toast.makeText(this, "Kata sandi baru tidak cocok", Toast.LENGTH_SHORT).show()
            } else {
                val sharedPrefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                val userId = sharedPrefs.getInt("user_id", -1)

                if (userId != -1) {
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            // First, get existing user data to maintain other fields (name, email, dates)
                            val userResponse = RetrofitInstance.api.getUser(userId) // Menggunakan RetrofitInstance.api
                            withContext(Dispatchers.Main) {
                                if (userResponse.isSuccessful) {
                                    val currentUser = userResponse.body()
                                    if (currentUser != null) {
                                        // Create request body with updated password
                                        val request = CreateUserRequest(
                                            nama = currentUser.nama,
                                            email = currentUser.email,
                                            password = newPassword, // This is the updated field
                                            tanggalDaftar = currentUser.tanggalDaftar,
                                            statusLangganan = currentUser.statusLangganan
                                        )

                                        CoroutineScope(Dispatchers.IO).launch {
                                            try {
                                                val updateResponse = RetrofitInstance.api.updateUser(userId, request) // Menggunakan RetrofitInstance.api
                                                withContext(Dispatchers.Main) {
                                                    if (updateResponse.isSuccessful) {
                                                        Toast.makeText(this@NewPasswordActivity, "Kata sandi berhasil diperbarui!", Toast.LENGTH_SHORT).show()
                                                        finish()
                                                    } else {
                                                        val errorBody = updateResponse.errorBody()?.string()
                                                        Toast.makeText(this@NewPasswordActivity, "Gagal memperbarui kata sandi: ${errorBody ?: updateResponse.message()}", Toast.LENGTH_LONG).show()
                                                    }
                                                }
                                            } catch (e: Exception) {
                                                withContext(Dispatchers.Main) {
                                                    Toast.makeText(this@NewPasswordActivity, "Error update kata sandi: ${e.message}", Toast.LENGTH_LONG).show()
                                                    e.printStackTrace()
                                                }
                                            }
                                        }
                                    } else {
                                        Toast.makeText(this@NewPasswordActivity, "Gagal memuat data pengguna untuk update password.", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    val errorBody = userResponse.errorBody()?.string()
                                    Toast.makeText(this@NewPasswordActivity, "Gagal memuat profil saat update password: ${errorBody ?: userResponse.message()}", Toast.LENGTH_LONG).show()
                                }
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(this@NewPasswordActivity, "Error saat memuat profil untuk update password: ${e.message}", Toast.LENGTH_LONG).show()
                                e.printStackTrace()
                            }
                        }
                    }
                } else {
                    Toast.makeText(this, "User ID tidak ditemukan. Tidak dapat memperbarui kata sandi.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}