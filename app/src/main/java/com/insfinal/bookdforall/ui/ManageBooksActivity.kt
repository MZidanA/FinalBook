package com.insfinal.bookdforall.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.layout.layout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ManageBooksActivity {
    package com.insfinal.bookdforall.ui // Sesuaikan dengan package Anda

    import android.content.Intent
    import android.os.Bundle
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.TextView
    import androidx.appcompat.app.AppCompatActivity
    import androidx.recyclerview.widget.LinearLayoutManager
    import androidx.recyclerview.widget.RecyclerView
    import com.google.android.material.floatingactionbutton.FloatingActionButton
    import com.google.android.material.snackbar.Snackbar
    import com.insfinal.bookdforall.R // Pastikan import R benar

    // Data class untuk merepresentasikan sebuah buku
    data class Book(
        val id: String,
        val title: String,
        val author: String,
        val isbn: String,
        val description: String? = null // Deskripsi opsional
    )

    class ManageBooksActivity : AppCompatActivity() {

        private lateinit var recyclerViewBooks: RecyclerView
        private lateinit var fabAddBook: androidx.compose.material3.FloatingActionButton
        private lateinit var bookAdapter: BookAdapter
        private val bookList = mutableListOf<Book>() // Daftar buku

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_manage_books)

            // 1. Setup Toolbar
            val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbarManageBooks)
            setSupportActionBar(toolbar)
            supportActionBar?.title = "Kelola Buku"
            supportActionBar?.setDisplayHomeAsUpEnabled(true) // Menampilkan tombol kembali
            supportActionBar?.setDisplayShowHomeEnabled(true)

            // 2. Inisialisasi Views
            recyclerViewBooks = findViewById(R.id.recyclerViewBooks)
            fabAddBook = findViewById(R.id.fabAddBook)

            // 3. Setup RecyclerView
            setupRecyclerView()

            // 4. Load data buku (Contoh data dummy, ganti dengan data dari database/API)
            loadDummyBooks()

            // 5. Setup FAB Click Listener
            fabAddBook.setOnClickListener {
                // TODO: Arahkan ke Activity atau Dialog untuk menambah buku baru
                // Contoh: startActivity(Intent(this, AddBookActivity::class.java))
                androidx.compose.material3.Snackbar.make(it, "Buka halaman tambah buku baru",
                    androidx.compose.material3.Snackbar.LENGTH_LONG).show()
            }
        }

        private fun setupRecyclerView() {
            bookAdapter = BookAdapter(bookList) { selectedBook ->
                // TODO: Handle klik pada item buku (misalnya, buka detail buku atau halaman edit)
                androidx.compose.material3.Snackbar.make(recyclerViewBooks, "Buku dipilih: ${selectedBook.title}",
                    androidx.compose.material3.Snackbar.LENGTH_SHORT).show()
                // Contoh:
                // val intent = Intent(this, BookDetailActivity::class.java)
                // intent.putExtra("BOOK_ID", selectedBook.id)
                // startActivity(intent)
            }
            recyclerViewBooks.layoutManager = LinearLayoutManager(this)
            recyclerViewBooks.adapter = bookAdapter
        }

        private fun loadDummyBooks() {
            // TODO: Ganti ini dengan logika untuk mengambil data buku dari sumber data Anda (Firebase, SQLite, API, dll.)
            bookList.clear() // Bersihkan list sebelum menambah data baru (jika dipanggil ulang)
            bookList.add(Book("1", "Belajar Kotlin dari Nol", "Ahmad Programmer", "978-1234567890", "Buku panduan lengkap untuk pemula."))
            bookList.add(Book("2", "Android Jetpack Mastery", "Budi Developer", "978-0987654321", "Menguasai komponen
}