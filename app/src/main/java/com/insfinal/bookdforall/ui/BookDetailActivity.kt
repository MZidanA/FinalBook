package com.insfinal.bookdforall.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.transition.Fade
import android.transition.TransitionManager
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.booksforall.databinding.ActivityBookDetailBinding // Pastikan import ini benar
import com.insfinal.bookdforall.model.Book // Import Book data class

class BookDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookDetailBinding
    private var isBookDownloaded: Boolean = false // Tambahkan status ini
    private val handler = Handler(Looper.getMainLooper())
    private var downloadProgress: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val book = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_BOOK, Book::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_BOOK)
        }

        if (book != null) {
            displayBookDetails(book)
            // Di sini Anda akan memeriksa status download buku yang sebenarnya
            // Misalnya, dari SharedPreferences, Room DB, atau server
            // Untuk demo, kita asumsikan awalnya belum didownload.
            // isBookDownloaded = checkBookDownloadStatus(book.bookId)
            updateButtonState(book) // Perbarui state tombol saat onCreate
        } else {
            Toast.makeText(this, "Gagal memuat detail buku.", Toast.LENGTH_SHORT).show()
            finish()
        }

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed() // Menggunakan modern back navigation
        }

        binding.btnDownload.setOnClickListener {
            val currentBook = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(EXTRA_BOOK, Book::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra(EXTRA_BOOK)
            }
            if (currentBook != null) {
                simulateDownload(currentBook)
            }
        }

        binding.btnBacaPratinjau.setOnClickListener {
            Toast.makeText(this, "Membuka Pratinjau untuk: ${book?.judul}", Toast.LENGTH_SHORT).show()
            // Implementasi logika baca pratinjau (misal: buka PDF viewer)
        }

        binding.btnBacaSingle.setOnClickListener {
            Toast.makeText(this, "Membuka buku: ${book?.judul}", Toast.LENGTH_SHORT).show()
            // Implementasi logika membuka buku setelah diunduh (misal: buka reader app)
        }

        binding.btnWriteReview.setOnClickListener {
            Toast.makeText(this, "Menulis ulasan untuk: ${book?.judul}", Toast.LENGTH_SHORT).show()
            // Implementasi logika menulis ulasan
        }
    }

    private fun displayBookDetails(book: Book) {
        Glide.with(this)
            .load(book.coverImageUrl)
            .placeholder(com.example.booksforall.R.drawable.placeholder_book_cover)
            .error(com.example.booksforall.R.drawable.placeholder_book_cover)
            .into(binding.ivBookCoverDetail)

        binding.tvBookTitleDetail.text = book.judul
        binding.tvBookAuthorDetail.text = book.penulis
        binding.tvBookDescription.text = book.deskripsi

        binding.ratingBar.rating = 4.8f // Contoh rating
        binding.tvRatingCount.text = "(230)" // Contoh count ulasan

        binding.tvInfoLanguage.text = "Indonesia"
        binding.tvInfoReleaseDate.text = "8 September 1999"
        binding.tvInfoPublisher.text = book.publisherId?.toString() ?: "N/A"
        binding.tvInfoAuthor.text = book.penulis
        binding.tvInfoPages.text = "544 Halaman"
        binding.tvInfoFormat.text = book.format
        binding.tvInfoCategory.text = book.kategori
    }

    // Fungsi untuk mensimulasikan proses download dan mengubah tombol
    private fun simulateDownload(book: Book) {
        // Nonaktifkan tombol dan tampilkan progress bar
        TransitionManager.beginDelayedTransition(binding.bottomButtonContainer, Fade())
        binding.layoutTwoButtons.visibility = View.GONE
        binding.btnBacaSingle.visibility = View.GONE
        binding.progressBarDownload.visibility = View.VISIBLE
        binding.progressBarDownload.progress = 0

        downloadProgress = 0
        val runnable = object : Runnable {
            override fun run() {
                if (downloadProgress < 100) {
                    downloadProgress += 10
                    binding.progressBarDownload.progress = downloadProgress
                    handler.postDelayed(this, 200) // Update progress every 200ms
                } else {
                    // Download selesai
                    isBookDownloaded = true
                    updateButtonState(book)
                    Toast.makeText(this@BookDetailActivity, "${book.judul} berhasil diunduh!", Toast.LENGTH_SHORT).show()
                }
            }
        }
        handler.post(runnable)
    }

    // Fungsi untuk memperbarui visibilitas tombol berdasarkan status download
    private fun updateButtonState(book: Book?) {
        val root = binding.detailRootLayout // Menggunakan id yang ditambahkan di XML
        TransitionManager.beginDelayedTransition(root, Fade())

        if (isBookDownloaded) {
            // Sembunyikan dua tombol awal dan progress bar
            binding.layoutTwoButtons.visibility = View.GONE
            binding.progressBarDownload.visibility = View.GONE
            // Tampilkan tombol "Baca" tunggal
            binding.btnBacaSingle.visibility = View.VISIBLE
        } else {
            // Sembunyikan tombol "Baca" tunggal dan progress bar
            binding.btnBacaSingle.visibility = View.GONE
            binding.progressBarDownload.visibility = View.GONE
            // Tampilkan dua tombol awal
            binding.layoutTwoButtons.visibility = View.VISIBLE
            // Pastikan tombol diaktifkan jika belum didownload
            binding.btnDownload.isEnabled = true
            binding.btnBacaPratinjau.isEnabled = true
        }
    }

    // Panggil updateButtonState saat Activity dibuat
    override fun onStart() {
        super.onStart()
        // Anda mungkin perlu memeriksa status download buku yang sebenarnya di sini
        // Misalnya, dari SharedPreferences atau Room DB, jika Anda menyimpannya.
        // isBookDownloaded = checkBookDownloadStatus(book.bookId) // Contoh
        // Untuk demo, kita bisa menginisialisasi ulang setiap kali onStart.
        // Namun dalam aplikasi nyata, status ini harus persisten.
        val book = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_BOOK, Book::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_BOOK)
        }
        if (book != null) {
            updateButtonState(book)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null) // Hapus callback untuk menghindari memory leak
    }

    companion object {
        const val EXTRA_BOOK = "extra_book"
    }

    // Contoh fungsi untuk memeriksa status download (perlu diimplementasikan secara nyata)
    // private fun checkBookDownloadStatus(bookId: Int): Boolean {
    //     // Implementasi logika untuk memeriksa apakah buku sudah diunduh
    //     // Misalnya: Cek di SharedPreferences atau database lokal
    //     return false // Mengembalikan false untuk tujuan demo
    // }
}