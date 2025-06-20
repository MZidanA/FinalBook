package com.insfinal.bookdforall.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.booksforall.R
import com.insfinal.bookdforall.model.User
import com.insfinal.bookdforall.repository.UserRepository
import kotlinx.coroutines.launch
import kotlin.jvm.java

class ProfileActivity : AppCompatActivity() {

    private lateinit var avatar: ImageView
    private var selectedImageUri: Uri? = null

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            avatar.setImageURI(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val tvNama = findViewById<TextView>(R.id.tv_nama)
        val tvEmail = findViewById<TextView>(R.id.tv_email)
        val btnUbahProfil = findViewById<Button>(R.id.btn_ubah_profil)
        val btnUbahPassword = findViewById<Button>(R.id.btn_ubah_password)
        val btnLogout = findViewById<Button>(R.id.btn_logout)

        avatar = findViewById(R.id.iv_avatar)

        lifecycleScope.launch {
            try {
                val response = UserRepository().getCurrentUser()
                if (response.isSuccessful) {
                    response.body()?.let { currentUser ->
                        tvNama.text = currentUser.nama
                        tvEmail.text = currentUser.email
                    }
                } else {
                    Log.e("ProfileActivity", "Gagal mendapatkan user: ${response.code()}")
                    // tvNama.text = user.nama // <- fallback opsional
                }
            } catch (e: Exception) {
                Log.e("ProfileActivity", "Error fetch user", e)
            }

        }

        if (selectedImageUri == null) {
            avatar.setImageURI(Uri.parse("android.resource://$packageName/drawable/profile"))
        }

        avatar.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        btnUbahProfil.setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
        }

        btnUbahPassword.setOnClickListener {
            startActivity(Intent(this, ChangePasswordActivity::class.java))
        }

        btnLogout.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Keluar")
                .setMessage("Apakah kamu yakin ingin logout?")
                .setPositiveButton("Ya") { _, _ ->
                    val sharedPref = getSharedPreferences("auth", MODE_PRIVATE)
                    sharedPref.edit().clear().apply()

                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
                .setNegativeButton("Batal", null)
                .show()
        }
    }
}