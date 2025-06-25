package com.insfinal.bookdforall.ui

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.insfinal.bookdforall.databinding.ActivityChangePasswordBinding
import com.insfinal.bookdforall.model.ErrorResponse
import com.insfinal.bookdforall.repository.UserRepository
import com.squareup.moshi.Moshi
import kotlinx.coroutines.launch

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangePasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnChangePassword.setOnClickListener {
            val oldPass = binding.etOldPassword.text.toString().trim()
            val newPass = binding.etNewPassword.text.toString().trim()
            val confirmPass = binding.etConfirmNewPassword.text.toString().trim()


            if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                Toast.makeText(this, "Semua kolom harus diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (newPass != confirmPass) {
                Toast.makeText(this, "Password baru tidak cocok", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // Panggil API ganti password
            binding.btnChangePassword.isEnabled = false
            binding.btnChangePassword.text = "Memproses..."
            changePassword(oldPass, newPass)
        }
        binding.btnKembali.setOnClickListener {
            finish()
        }
    }

    private fun changePassword(oldPass: String, newPass: String) {
        lifecycleScope.launch {
            try {
                val response = UserRepository().changePassword(oldPass, newPass)
                if (response.isSuccessful) {
                    Toast.makeText(this@ChangePasswordActivity, "Password berhasil diubah", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    val rawErrorBody = response.errorBody()?.string()
                    val moshi = Moshi.Builder().build()
                    val adapter = moshi.adapter(ErrorResponse::class.java)
                    val error = adapter.fromJson(rawErrorBody ?: "")
                    val errorMessage = error?.message ?: rawErrorBody ?: "Terjadi kesalahan"
                    Toast.makeText(this@ChangePasswordActivity, "Gagal: $errorMessage", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("ChangePassword", "Error", e)
            } finally {
                binding.btnChangePassword.isEnabled = true
                binding.btnChangePassword.text = "Ganti Password"
            }
        }
    }
}