package com.insfinal.bookdforall.ui

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.booksforall.databinding.ActivityForgotPasswordBinding
import com.insfinal.bookdforall.repository.AuthRepository
import kotlinx.coroutines.launch

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSendResetLink.setOnClickListener {
            val email = binding.editTextEmail.text.toString().trim()

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Format email tidak valid.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                lifecycleScope.launch {
                    try {
                        val response = AuthRepository().forgotPassword(email)
                        if (response.isSuccessful) {
                            Toast.makeText(this@ForgotPasswordActivity, "Silakan atur password baru Anda.", Toast.LENGTH_LONG).show()

                            val intent = Intent(this@ForgotPasswordActivity, SetNewPasswordActivity::class.java)
                            intent.putExtra("email", email)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this@ForgotPasswordActivity, "Email tidak ditemukan.", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this@ForgotPasswordActivity, "Terjadi kesalahan: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        binding.btnKembali.setOnClickListener {
            finish()
        }
    }
}