package com.insfinal.bookdforall.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.layout.layout
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.text
import androidx.glance.visibility
import com.google.android.material.textfield.TextInputLayout
import com.insfinal.bookdforall.R // Pastikan import R benar

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var textInputLayoutNewPassword: TextInputLayout
    private lateinit var editTextNewPassword: EditText
    private lateinit var textInputLayoutConfirmPassword: TextInputLayout
    private lateinit var editTextConfirmPassword: EditText
    private lateinit var buttonSaveNewPassword: Button
    private lateinit var progressBar: ProgressBar

    // Anda mungkin menerima token atau email dari Activity sebelumnya (ForgotPasswordActivity)
    // Untuk simulasi, kita bisa abaikan ini atau gunakan nilai dummy
    private var resetToken: String? = null
    private var userEmail: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        // Ambil data dari Intent jika ada (misalnya token atau email)
        // resetToken = intent.getStringExtra("RESET_TOKEN")
        // userEmail = intent.getStringExtra("USER_EMAIL")

        // Setup Toolbar (jika ada di layout Anda)
        // Misal, jika Anda punya toolbar dengan ID toolbarResetPassword:
        // val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbarResetPassword)
        // setSupportActionBar(toolbar)
        supportActionBar?.title = "Atur Password Baru" // Atau "Reset Password"
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Untuk tombol kembali

        textInputLayoutNewPassword = findViewById(com.insfinal.bookdforall.ui                                               .textInputLayoutNewPassword)
        editTextNewPassword = findViewById(R.id.editTextNewPassword)
        textInputLayoutConfirmPassword = findViewById(R.id.textInputLayoutConfirmPassword)
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword)
        buttonSaveNewPassword = findViewById(R.id.buttonSaveNewPassword)
        progressBar = findViewById(R.id.progressBarResetPassword) // Pastikan ID ini ada di XML

        buttonSaveNewPassword.setOnClickListener {
            val newPassword = editTextNewPassword.text.toString()
            val confirmPassword = editTextConfirmPassword.text.toString()
            validateAndSimulateReset(newPassword, confirmPassword)
        }
    }

    private fun validateAndSimulateReset(newPass: String, confirmPass: String) {
        // Hapus error sebelumnya
        textInputLayoutNewPassword.error = null
        textInputLayoutConfirmPassword.error = null

        if (newPass.isEmpty()) {
            textInputLayoutNewPassword.error = "Password baru tidak boleh kosong"
            return
        }

        // Opsional: Validasi kekuatan password (contoh: minimal 6 karakter)
        if (newPass.length < 6) {
            textInputLayoutNewPassword.error = "Password minimal 6 karakter"
            return
        }

        if (confirmPass.isEmpty()) {
            textInputLayoutConfirmPassword.error = "Verifikasi password tidak boleh kosong"
            return
        }

        if (newPass != confirmPass) {
            textInputLayoutConfirmPassword.error = "Password tidak cocok"
            // Anda mungkin juga ingin menandai error di field newPass jika diperlukan
            // textInputLayoutNewPassword.error = " " // Memberi tanda error tanpa teks
            return
        }

        // Semua validasi lolos, simulasikan penyimpanan
        showLoading(true)

        // Handler untuk menunda aksi (simulasi latensi jaringan/database)
        Handler(Looper.getMainLooper()).postDelayed({
            showLoading(false)

            // SIMULASI: Anggap proses reset berhasil
            // Di sini, jika ada backend, Anda akan mengirim newPass (dan mungkin token/email) ke server
            // untuk memperbarui password pengguna.

            Toast.makeText(this, "Password berhasil direset! (simulasi)", Toast.LENGTH_LONG).show()

            // Arahkan kembali ke halaman Login setelah reset berhasil
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Hapus back stack
            startActivity(intent)
            finish() // Tutup activity ini

        }, 2000) // Tunda selama 2 detik
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            progressBar.visibility = View.VISIBLE
            buttonSaveNewPassword.isEnabled = false
            // Opsional: Nonaktifkan input fields juga
            // editTextNewPassword.isEnabled = false
            // editTextConfirmPassword.isEnabled = false
        } else {
            progressBar.visibility = View.GONE
            buttonSaveNewPassword.isEnabled = true
            // editTextNewPassword.isEnabled = true
            // editTextConfirmPassword.isEnabled = true
        }
    }

    // Menangani aksi tombol kembali di Toolbar
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed() // Atau navigasi spesifik jika diperlukan
        return true
    }
}