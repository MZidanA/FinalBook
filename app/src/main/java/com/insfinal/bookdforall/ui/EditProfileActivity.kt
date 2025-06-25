package com.insfinal.bookdforall.ui

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.insfinal.bookdforall.R
import com.insfinal.bookdforall.model.CreateUserRequest
import com.insfinal.bookdforall.model.User
import com.insfinal.bookdforall.repository.UserRepository
import kotlinx.coroutines.launch

class EditProfileActivity : AppCompatActivity() {

    private lateinit var etNama: EditText
    private lateinit var etEmail: EditText
    private lateinit var btnSimpan: Button

    fun User.toCreateUserRequest(nama: String, email: String) = CreateUserRequest(
        nama = nama,
        email = email,
        password = this.password ?: "",
        tanggalDaftar = this.tanggalDaftar,
        statusLangganan = this.statusLangganan
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        etNama = findViewById(R.id.et_nama)
        etEmail = findViewById(R.id.et_email)
        btnSimpan = findViewById(R.id.btn_simpan)

        // Fetch current profile
        lifecycleScope.launch {
            try {
                val response = UserRepository().getCurrentUser()
                if (response.isSuccessful) {
                    response.body()?.let { user ->
                        etNama.setText(user.nama)
                        etEmail.setText(user.email)
                    }
                }
            } catch (e: Exception) {
                Log.e("EditProfile", "Gagal ambil data user", e)
            }
        }

        btnSimpan.setOnClickListener {
            val namaBaru = etNama.text.toString().trim()
            val emailBaru = etEmail.text.toString().trim()

            lifecycleScope.launch {
                try {
                    val currentUser = UserRepository().getCurrentUser().body()
                    currentUser?.let {
                        val response = UserRepository().updateUser(
                            it.userId,
                            it.toCreateUserRequest(nama = namaBaru, email = emailBaru)
                        )
                        if (response.isSuccessful) {
                            Toast.makeText(this@EditProfileActivity, "Berhasil disimpan", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(this@EditProfileActivity, "Gagal menyimpan", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    Log.e("EditProfile", "Gagal update", e)
                }
            }
        }
        val btnKembali: ImageButton = findViewById(R.id.btn_kembali)
        btnKembali.setOnClickListener {
            finish()
        }
    }
}