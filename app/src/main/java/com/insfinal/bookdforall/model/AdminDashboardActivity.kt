package com.bookdforall // <<< GANTI DENGAN NAMA PACKAGE ANDA YANG SEBENARNYA

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.button.MaterialButton
import com.insfinal.bookdforall.R


class AdminDashboardActivity : AppCompatActivity() {

    // Deklarasi variabel untuk setiap View yang memiliki ID di layout
    private lateinit var toolbar: Toolbar
    private lateinit var totalUsersTextView: TextView
    private lateinit var totalBooksTextView: TextView
    private lateinit var pendingRequestsTextView: TextView
    private lateinit var newReportsTextView: TextView
    private lateinit var manageUsersButton: MaterialButton
    private lateinit var manageBooksButton: MaterialButton
    private lateinit var manageRequestsButton: MaterialButton
    private lateinit var manageForumButton: MaterialButton
    private lateinit var logoutButton: MaterialButton // Tombol logout yang baru

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Menghubungkan Activity ini dengan layout XML: activity_admin_dashboard.xml
        setContentView(R.layout.activity_admin_dashboard)

        // 1. Inisialisasi Toolbar
        toolbar = findViewById(R.id.toolbarAdminDashboard)
        setSupportActionBar(toolbar) // Mengatur toolbar sebagai ActionBar
        supportActionBar?.apply { // Menggunakan apply scope function untuk null safety
            title = "Admin Dashboard" // Mengatur judul toolbar
        }
        // Mengatur warna teks judul toolbar (bisa juga dari colors.xml)
        toolbar.setTitleTextColor(Color.parseColor("#FBAF3F"))


        // 2. Inisialisasi TextViews untuk Ringkasan Aplikasi
        totalUsersTextView = findViewById(R.id.totalUsersTextView)
        totalBooksTextView = findViewById(R.id.totalBooksTextView)
        pendingRequestsTextView = findViewById(R.id.pendingRequestsTextView)
        newReportsTextView = findViewById(R.id.newReportsTextView)

        // TODO: Ambil data aktual dari database/API (misalnya Firebase Firestore)
        // Ini hanya contoh data statis:
        totalUsersTextView.text = "1,250"
        totalBooksTextView.text = "342"
        pendingRequestsTextView.text = "15"
        newReportsTextView.text = "8"

        // 3. Inisialisasi MaterialButtons untuk Menu Manajemen
        manageUsersButton = findViewById(R.id.manageUsersButton)
        manageBooksButton = findViewById(R.id.manageBooksButton)

        logoutButton = findViewById(R.id.logoutButton) // Inisialisasi tombol logout

        // 4. Menambahkan Click Listeners untuk Tombol-tombol
        manageUsersButton.setOnClickListener {
            // TODO: Tambahkan logika navigasi ke Activity "Kelola Pengguna"
            // Contoh: startActivity(Intent(this, ManageUsersActivity::class.java))
            // Toast.makeText(this, "Kelola Pengguna clicked", Toast.LENGTH_SHORT).show()
        }

        manageBooksButton.setOnClickListener {
            // TODO: Tambahkan logika navigasi ke Activity "Kelola Buku"
            // Contoh: startActivity(Intent(this, ManageBooksActivity::class.java))
            // Toast.makeText(this, "Kelola Buku clicked", Toast.LENGTH_SHORT).show()
        }

        manageRequestsButton.setOnClickListener {
            // TODO: Tambahkan logika navigasi ke Activity "Kelola Permintaan"
            // Contoh: startActivity(Intent(this, ManageRequestsActivity::class.java))
            // Toast.makeText(this, "Kelola Permintaan clicked", Toast.LENGTH_SHORT).show()
        }

        manageForumButton.setOnClickListener {
            // TODO: Tambahkan logika navigasi ke Activity "Kelola Forum"
            // Contoh: startActivity(Intent(this, ManageForumActivity::class.java))
            // Toast.makeText(this, "Kelola Forum clicked", Toast.LENGTH_SHORT).show()
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