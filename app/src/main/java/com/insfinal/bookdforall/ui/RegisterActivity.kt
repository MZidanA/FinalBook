// RegisterActivity.kt
package com.insfinal.bookdforall.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.insfinal.bookdforall.databinding.ActivityRegisterBinding
import com.insfinal.bookdforall.network.RetrofitInstance // Import RetrofitInstance
import com.insfinal.bookdforall.model.CreateUserRequest // Import CreateUserRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

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
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val tanggalDaftar = sdf.format(Date())
                        val request = CreateUserRequest(name, email, password, tanggalDaftar, "free") // Default status_langganan

                        val response = RetrofitInstance.api.createUser(request) // Menggunakan RetrofitInstance.api

                        withContext(Dispatchers.Main) {
                            if (response.isSuccessful) {
                                Toast.makeText(this@RegisterActivity, "Pendaftaran berhasil untuk: $email", Toast.LENGTH_LONG).show()
                                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                val errorBody = response.errorBody()?.string()
                                Toast.makeText(this@RegisterActivity, "Pendaftaran gagal: ${errorBody ?: response.message()}", Toast.LENGTH_LONG).show()
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@RegisterActivity, "Error pendaftaran: ${e.message}", Toast.LENGTH_LONG).show()
                            e.printStackTrace()
                        }
                    }
                }
            }
        }

        binding.tvLoginLink.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}