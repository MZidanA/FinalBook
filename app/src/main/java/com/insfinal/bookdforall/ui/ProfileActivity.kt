package com.insfinal.bookdforall.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.insfinal.bookdforall.databinding.ActivityProfileBinding
import com.bumptech.glide.Glide
import com.insfinal.bookdforall.R
import com.insfinal.bookdforall.network.RetrofitInstance // Import RetrofitInstance
import com.insfinal.bookdforall.model.User // Import User model
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadProfileData() // Load data when the activity is created

        binding.btnUbahProfil.setOnClickListener {
            val sharedPrefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            val userId = sharedPrefs.getInt("user_id", -1)
            if (userId != -1) {
                val intent = Intent(this, EditProfileActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Silakan login untuk mengubah profil.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnUbahPassword.setOnClickListener {
            val sharedPrefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            val userId = sharedPrefs.getInt("user_id", -1)
            if (userId != -1) {
                val intent = Intent(this, ChangePasswordActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Silakan login untuk mengubah kata sandi.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnLogout.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = RetrofitInstance.api.logout() // Menggunakan RetrofitInstance.api
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            val sharedPrefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                            sharedPrefs.edit()
                                .clear() // Clear all user data including ID
                                .apply()

                            Toast.makeText(this@ProfileActivity, "Berhasil keluar!", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@ProfileActivity, LoginActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
                        } else {
                            val errorBody = response.errorBody()?.string()
                            Toast.makeText(this@ProfileActivity, "Gagal keluar: ${errorBody ?: response.message()}", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@ProfileActivity, "Error keluar: ${e.message}", Toast.LENGTH_SHORT).show()
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    private fun loadProfileData() {
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
                                binding.tvNama.text = user.nama
                                binding.tvEmail.text = user.email
                                // avatarUrl tidak ada di model User Anda, jadi tetap gunakan dari SharedPreferences atau default
                                val avatarUrl = sharedPrefs.getString("user_avatar_url", "")
                                if (!avatarUrl.isNullOrEmpty() && avatarUrl.startsWith("http")) {
                                    Glide.with(this@ProfileActivity)
                                        .load(avatarUrl)
                                        .placeholder(R.drawable.ic_profile)
                                        .error(R.drawable.ic_profile)
                                        .circleCrop()
                                        .into(binding.ivAvatar)
                                } else {
                                    binding.ivAvatar.setImageResource(R.drawable.ic_profile)
                                }
                            } else {
                                Toast.makeText(this@ProfileActivity, "Data pengguna kosong.", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            val errorBody = response.errorBody()?.string()
                            Toast.makeText(this@ProfileActivity, "Gagal memuat profil: ${errorBody ?: response.message()}", Toast.LENGTH_LONG).show()
                            // Jika data user tidak dapat dimuat, pertimbangkan untuk log out atau meminta login ulang
                            // handleLogout() // Contoh: panggil fungsi logout
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@ProfileActivity, "Error memuat profil: ${e.message}", Toast.LENGTH_LONG).show()
                        e.printStackTrace()
                        // handleLogout() // Contoh: panggil fungsi logout
                    }
                }
            }
        } else {
            Toast.makeText(this, "Anda belum login atau ID pengguna tidak ditemukan.", Toast.LENGTH_LONG).show()
            binding.tvNama.text = "Tamu"
            binding.tvEmail.text = "Silakan login"
            binding.ivAvatar.setImageResource(R.drawable.ic_profile)
            // Redirect ke LoginActivity jika belum login
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        loadProfileData() // Muat ulang data user saat Activity kembali ke foreground
    }
}