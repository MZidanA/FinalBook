// LoginActivity.kt
package com.insfinal.bookdforall.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.insfinal.bookdforall.databinding.ActivityLoginBinding
import com.insfinal.bookdforall.network.RetrofitInstance // Import RetrofitInstance
import com.insfinal.bookdforall.model.LoginRequest // Import LoginRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonLogin.setOnClickListener {
            val email = binding.editTextEmailLogin.text.toString().trim()
            val password = binding.editTextPasswordLogin.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan password tidak boleh kosong", Toast.LENGTH_SHORT).show()
            } else {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val request = LoginRequest(email, password)
                        val response = RetrofitInstance.api.login(request) // Menggunakan RetrofitInstance.api

                        withContext(Dispatchers.Main) {
                            if (response.isSuccessful) {
                                val loginResponse = response.body()
                                if (loginResponse != null) {
                                    // Simpan user_id, nama, email, dan status login ke SharedPreferences
                                    val sharedPrefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                                    sharedPrefs.edit()
                                        .putBoolean("is_logged_in", true)
                                        .putInt("user_id", loginResponse.user.id)
                                        .putString("user_name", loginResponse.user.nama)
                                        .putString("user_email", loginResponse.user.email)
                                        .apply()

                                    Toast.makeText(this@LoginActivity, loginResponse.message, Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    Toast.makeText(this@LoginActivity, "Login berhasil, namun respons user kosong.", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                val errorBody = response.errorBody()?.string()
                                Toast.makeText(this@LoginActivity, "Login gagal: ${errorBody ?: response.message()}", Toast.LENGTH_LONG).show()
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@LoginActivity, "Error koneksi: ${e.message}", Toast.LENGTH_LONG).show()
                            e.printStackTrace()
                        }
                    }
                }
            }
        }

        binding.textViewForgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        binding.textViewRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}