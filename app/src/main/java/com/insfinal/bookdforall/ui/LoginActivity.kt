package com.insfinal.bookdforall.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.insfinal.bookdforall.databinding.ActivityLoginBinding
import com.insfinal.bookdforall.utils.SessionManager // <-- Pastikan ini diimport
import android.util.Log // Import Log untuk debugging

// Import Retrofit dan model terkait jika suatu saat ingin mengaktifkan login API
// import com.insfinal.bookdforall.network.RetrofitInstance
// import com.insfinal.bookdforall.model.LoginRequest
// import com.insfinal.bookdforall.model.LoginResponse
// import kotlinx.coroutines.CoroutineScope
// import kotlinx.coroutines.Dispatchers
// import kotlinx.coroutines.launch
// import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    // --- Kredensial Admin Dummy ---
    private val ADMIN_EMAIL = "admin@example.com"
    private val ADMIN_PASSWORD = "adminpassword"

    // --- Kredensial User Test Dummy ---
    private val USER_TEST_EMAIL = "test@example.com"
    private val USER_TEST_PASSWORD = "password123"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Cek apakah user sudah login (termasuk admin) menggunakan SessionManager
        if (SessionManager.isLoggedIn()) {
            if (SessionManager.isAdmin()) {
                navigateToAdminHome()
            } else {
                navigateToUserHome()
            }
            finish() // Tutup LoginActivity agar tidak bisa kembali setelah login
            return
        }

        binding.buttonLogin.setOnClickListener {
            val email = binding.editTextEmailLogin.text.toString().trim()
            val password = binding.editTextPasswordLogin.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan password tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener // Penting untuk keluar dari listener
            }

            // --- JALUR 1: LOGIN ADMIN HARDCODED ---
            if (email == ADMIN_EMAIL && password == ADMIN_PASSWORD) {
                SessionManager.createLoginSession(email, true) // Simpan status sebagai admin
                Toast.makeText(this@LoginActivity, "Login Admin Berhasil!", Toast.LENGTH_SHORT).show()
                navigateToAdminHome()
                finish()
                return@setOnClickListener
            }

            // --- JALUR 2: LOGIN USER TEST HARDCODED (yang sudah ada) ---
            if (email == USER_TEST_EMAIL && password == USER_TEST_PASSWORD) {
                // Untuk test user, Anda bisa menggunakan email sebagai token dummy atau ID user.
                // SessionManager akan menyimpan status is_logged_in = true dan is_admin = false.
                SessionManager.createLoginSession(email, false) // Simpan status sebagai user biasa
                Toast.makeText(this@LoginActivity, "Login berhasil!", Toast.LENGTH_SHORT).show()
                navigateToUserHome()
                finish()
                return@setOnClickListener
            }

            // --- JALUR 3: LOGIN VIA API (saat ini dikomentari) ---
            Toast.makeText(this@LoginActivity, "Email atau password salah. (Jika bukan admin/test user, API tidak aktif)", Toast.LENGTH_LONG).show()
            Log.d("LoginActivity", "Attempted API login but it's commented out.")

            /* // KODE API YANG DIKOMENTARI (tidak diubah dalam konteks ini)
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val request = LoginRequest(email, password)
                    val response = RetrofitInstance.api.login(request)

                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            val loginResponse = response.body()
                            if (loginResponse != null) {
                                // Pastikan LoginResponse Anda memiliki properti untuk token,
                                // misalnya 'accessToken' atau 'token'
                                val authToken = loginResponse.accessToken // Sesuaikan dengan nama properti token di LoginResponse Anda
                                if (authToken != null) {
                                    SessionManager.createLoginSession(authToken, loginResponse.user.isAdmin)
                                    Toast.makeText(this@LoginActivity, loginResponse.message, Toast.LENGTH_SHORT).show()
                                    if (loginResponse.user.isAdmin) {
                                        navigateToAdminHome()
                                    } else {
                                        navigateToUserHome()
                                    }
                                    finish()
                                } else {
                                    Toast.makeText(this@LoginActivity, "Login berhasil, namun token tidak ditemukan.", Toast.LENGTH_SHORT).show()
                                    Log.e("LoginActivity", "Login successful but auth token is null.")
                                }
                            } else {
                                Toast.makeText(this@LoginActivity, "Login berhasil, namun respons user kosong.", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            val errorBody = response.errorBody()?.string()
                            Toast.makeText(this@LoginActivity, "Login gagal: ${errorBody ?: response.message()}", Toast.LENGTH_LONG).show()
                            Log.e("LoginActivity", "API Login failed: ${response.code()} - ${errorBody ?: response.message()}")
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@LoginActivity, "Error koneksi: ${e.message}", Toast.LENGTH_LONG).show()
                        Log.e("LoginActivity", "API Login exception: ${e.message}", e)
                        e.printStackTrace()
                    }
                }
            }
            */
        }

        binding.textViewForgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        binding.textViewRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun navigateToUserHome() {
        startActivity(Intent(this, MainActivity::class.java))
    }

    private fun navigateToAdminHome() {
        startActivity(Intent(this, AdminActivity::class.java))
    }
}