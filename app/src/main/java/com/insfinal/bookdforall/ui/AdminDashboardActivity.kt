package com.insfinal.bookdforall.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.button.MaterialButton
//import com.insfinal.bookdforall.R
import com.insfinal.booksforall.databinding.ActivityAdminDashboardBinding

class AdminDashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminDashboardBinding
    // Deklarasi variabel untuk setiap View yang memiliki ID di layout
    private lateinit var toolbar: Toolbar
    private lateinit var totalUsersTextView: TextView
    private lateinit var totalBooksTextView: TextView
    private lateinit var newReportsTextView: TextView
    private lateinit var manageBooksButton: MaterialButton
    private lateinit var manageRequestsButton: MaterialButton
    private lateinit var logoutButton: MaterialButton // Tombol logout yang baru

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Menghubungkan Activity ini dengan layout XML: activity_admin_dashboard.xml
        setContentView(com.insfinal.booksforall.R.layout.activity_admin_dashboard)

        // 1. Inisialisasi Toolbar
        toolbar = findViewById(com.insfinal.booksforall.R.id.toolbarAdminDashboard)
        setSupportActionBar(toolbar) // Mengatur toolbar sebagai ActionBar
        supportActionBar?.apply { // Menggunakan apply scope function untuk null safety
            title = "Admin Dashboard" // Mengatur judul toolbar
        }
        // Mengatur warna teks judul toolbar (bisa juga dari colors.xml)
        toolbar.setTitleTextColor(Color.parseColor("#FBAF3F"))


        // 2. Inisialisasi TextViews untuk Ringkasan Aplikasi
        totalUsersTextView = findViewById(com.insfinal.booksforall.R.id.totalUsersTextView)
        totalBooksTextView = findViewById(com.insfinal.booksforall.R.id.totalBooksTextView)
        totalBooksTextView = findViewById(com.insfinal.booksforall.R.id.totalBooksTextView)
        newReportsTextView = findViewById(com.insfinal.booksforall.R.id.newReportsTextView)

        // TODO: Ambil data aktual dari database/API (misalnya Firebase Firestore)
        // Ini hanya contoh data statis:
        totalUsersTextView.text = "1,250"
        totalBooksTextView.text = "342"
        newReportsTextView.text = "8"



        // 4. Menambahkan Click Listeners untuk Tombol-tombol
        binding.manageUsersButton.setOnClickListener {
            // Arahkan ke ManageUserActivity
            val intent = Intent(this, ManageUserActivity::class.java)
            startActivity(intent)
        }

        binding.manageBooksButton.setOnClickListener {
            // Arahkan ke ManageBooksActivity
            val intent = Intent(this, ManageBooksActivity::class.java)
            startActivity(intent)
        }



        logoutButton.setOnClickListener {
            // TODO: Tambahkan logika untuk proses logout
            // Ini bisa melibatkan:
            // 1. Menghapus token otentikasi (misalnya Firebase Auth.signOut())
            // 2. Menghapus data sesi lokal
            // 3. Menavigasi kembali ke layar Login/SplashScreen
            // Contoh:
            // FirebaseAuth.getInstance().signOut()
            // val intent = Intent(this, LoginActivity::class.java)
            // intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            // startActivity(intent)
            // finish()
            // Toast.makeText(this, "Logout clicked", Toast.LENGTH_SHORT).show()
        }
    }
}