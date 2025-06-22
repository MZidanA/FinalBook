package com.insfinal.bookdforall.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.booksforall.R
import com.example.booksforall.databinding.ActivitySetNewPasswordBinding
import com.insfinal.bookdforall.repository.AuthRepository
import kotlinx.coroutines.launch

class SetNewPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySetNewPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetNewPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSetPassword.setOnClickListener {
            val newPassword = binding.etNewPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()
            val email = intent.getStringExtra("email")
            if (email.isNullOrEmpty()) {
                toast("Email tidak tersedia.")
                return@setOnClickListener
            }
            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                toast("Password tidak boleh kosong.")
            } else if (newPassword != confirmPassword) {
                toast("Konfirmasi password tidak cocok.")
            } else {
                lifecycleScope.launch {
                    try {
                        val response = AuthRepository().setNewPassword(email, newPassword)
                        if (response.isSuccessful) {
                            toast("Password berhasil diperbarui.")

                            val intent = Intent(this@SetNewPasswordActivity, LoginActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)

                            finish()
                        } else {
                            toast("Gagal memperbarui password.")
                        }
                    } catch (e: Exception) {
                        toast("Terjadi kesalahan: ${e.message}")
                    }
                }
            }
        }
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}