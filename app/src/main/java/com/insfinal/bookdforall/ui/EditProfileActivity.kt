package com.insfinal.bookdforall.ui

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.insfinal.bookdforall.databinding.ActivityEditProfileBinding
import com.insfinal.bookdforall.network.RetrofitInstance // Use RetrofitInstance
import com.insfinal.bookdforall.model.User // For fetching existing data
import com.insfinal.bookdforall.model.CreateUserRequest // For update payload
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Fetch fresh data from API to ensure consistency
        loadUserDataForEdit()

        binding.btnKembali.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnSimpan.setOnClickListener {
            val newName = binding.etNama.text.toString().trim()
            val newEmail = binding.etEmail.text.toString().trim()

            if (newName.isEmpty() || newEmail.isEmpty()) {
                Toast.makeText(this, "Nama dan Email tidak boleh kosong", Toast.LENGTH_SHORT).show()
            } else {
                val sharedPrefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                val userId = sharedPrefs.getInt("user_id", -1)

                if (userId != -1) {
                    // Fetch current user data to get password, tanggal_daftar, status_langganan
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val userResponse = RetrofitInstance.api.getUser(userId) // Menggunakan RetrofitInstance.api
                            withContext(Dispatchers.Main) {
                                if (userResponse.isSuccessful) {
                                    val currentUser = userResponse.body()
                                    if (currentUser != null) {
                                        // Create request body for update
                                        // IMPORTANT: The `password` field in `CreateUserRequest` is required by your Swagger schema.
                                        // You need to send the existing password back.
                                        val request = CreateUserRequest(
                                            nama = newName,
                                            email = newEmail,
                                            password = currentUser.password, // Use existing password
                                            tanggalDaftar = currentUser.tanggalDaftar, // Use existing tanggal_daftar
                                            statusLangganan = currentUser.statusLangganan // Use existing status_langganan
                                        )

                                        CoroutineScope(Dispatchers.IO).launch {
                                            try {
                                                val updateResponse = RetrofitInstance.api.updateUser(userId, request) // Menggunakan RetrofitInstance.api
                                                withContext(Dispatchers.Main) {
                                                    if (updateResponse.isSuccessful) {
                                                        // Update SharedPreferences after successful backend update
                                                        sharedPrefs.edit()
                                                            .putString("user_name", newName)
                                                            .putString("user_email", newEmail)
                                                            .apply()
                                                        Toast.makeText(this@EditProfileActivity, "Profil berhasil diperbarui", Toast.LENGTH_SHORT).show()
                                                        finish()
                                                    } else {
                                                        val errorBody = updateResponse.errorBody()?.string()
                                                        Toast.makeText(this@EditProfileActivity, "Gagal memperbarui profil: ${errorBody ?: updateResponse.message()}", Toast.LENGTH_LONG).show()
                                                    }
                                                }
                                            } catch (e: Exception) {
                                                withContext(Dispatchers.Main) {
                                                    Toast.makeText(this@EditProfileActivity, "Error update profil: ${e.message}", Toast.LENGTH_LONG).show()
                                                    e.printStackTrace()
                                                }
                                            }
                                        }
                                    } else {
                                        Toast.makeText(this@EditProfileActivity, "Gagal memuat data pengguna untuk diupdate.", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    val errorBody = userResponse.errorBody()?.string()
                                    Toast.makeText(this@EditProfileActivity, "Gagal memuat profil saat edit: ${errorBody ?: userResponse.message()}", Toast.LENGTH_LONG).show()
                                }
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(this@EditProfileActivity, "Error saat memuat profil untuk edit: ${e.message}", Toast.LENGTH_LONG).show()
                                e.printStackTrace()
                            }
                        }
                    }
                } else {
                    Toast.makeText(this, "User ID tidak ditemukan. Tidak dapat memperbarui profil.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun loadUserDataForEdit() {
        val sharedPrefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userId = sharedPrefs.getInt("user_id", -1)

        if (userId != -1) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = RetrofitInstance.api.getUser(userId) // Menggunakan RetrofitInstance.api
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            val user = response.body()
                            if (user != null) {
                                binding.etNama.setText(user.nama)
                                binding.etEmail.setText(user.email)
                            } else {
                                Toast.makeText(this@EditProfileActivity, "Data pengguna kosong.", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            val errorBody = response.errorBody()?.string()
                            Toast.makeText(this@EditProfileActivity, "Gagal memuat data pengguna: ${errorBody ?: response.message()}", Toast.LENGTH_LONG).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@EditProfileActivity, "Error memuat data pengguna: ${e.message}", Toast.LENGTH_LONG).show()
                        e.printStackTrace()
                    }
                }
            }
        } else {
            Toast.makeText(this, "User ID tidak ditemukan. Tidak dapat memuat profil.", Toast.LENGTH_SHORT).show()
            // Jika ID tidak ada, bisa diisi dengan data yang dilalui dari Intent jika ada
            binding.etNama.setText(intent.getStringExtra("current_name") ?: "")
            binding.etEmail.setText(intent.getStringExtra("current_email") ?: "")
        }
    }
}