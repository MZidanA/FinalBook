package com.insfinal.bookdforall.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.transition.Fade
import android.transition.TransitionManager
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.booksforall.databinding.ActivityBookDetailBinding
import com.insfinal.bookdforall.model.Book
import com.insfinal.bookdforall.repository.BookRepository
import kotlinx.coroutines.launch
import java.io.File

class BookDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookDetailBinding
    private var isBookDownloaded: Boolean = false // Tambahkan status ini
    private val handler = Handler(Looper.getMainLooper())
    private var downloadProgress: Int = 0
    private var book: Book? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bookId = intent.getIntExtra(EXTRA_BOOK_ID, -1)
        if (bookId != -1) {
            fetchBookById(bookId)
        } else {
            showError("ID buku tidak valid.")
        }

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed() // Menggunakan modern back navigation
        }

        binding.btnDownload.setOnClickListener {
            book?.let { simulateDownload(it) }
        }

        binding.btnBacaPratinjau.setOnClickListener {
            Toast.makeText(this, "Membuka Pratinjau untuk: ${book?.judul}", Toast.LENGTH_SHORT).show()
            // Implementasi logika baca pratinjau (misal: buka PDF viewer)
        }

        binding.btnBacaSingle.setOnClickListener {
            book?.let {
                val file = File(getExternalFilesDir(null), "book_${it.bookId}.pdf")
                if (file.exists()) {
                    val uri = FileProvider.getUriForFile(
                        this,
                        "${packageName}.provider",
                        file
                    )

                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.setDataAndType(uri, "application/pdf")
                    intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "File buku tidak ditemukan.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnWriteReview.setOnClickListener {
            Toast.makeText(this, "Menulis ulasan untuk: ${book?.judul}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchBookById(bookId: Int) {
        lifecycleScope.launch {
            try {
                val response = BookRepository().getBookById(bookId)
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null) {
                        book = result
                        displayBookDetails(result)
                        updateButtonState(result)
                    } else {
                        showError("Buku tidak ditemukan.")
                    }
                } else {
                    showError("Gagal memuat buku. Kode: ${response.code()}")
                }
            } catch (e: Exception) {
                showError("Terjadi kesalahan: ${e.message}")
            }
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

        binding.ratingBar.rating = book.rating ?: 0f
        binding.tvRatingCount.text = "(${book.ratingCount ?: 0})"

        binding.tvInfoLanguage.text = "Indonesia"
        binding.tvInfoReleaseDate.text = book.releaseDate ?: "-"
        binding.tvInfoPublisher.text = book.publisherId?.toString() ?: "N/A"
        binding.tvInfoAuthor.text = book.penulis
        binding.tvInfoPages.text = "${book.totalPages ?: "?"} Halaman"
        binding.tvInfoCategory.text = book.kategori
        binding.tvInfoFormat.text = book.format.takeIf { !it.isNullOrBlank() } ?: "-"
    }

    private fun simulateDownload(book: Book) {
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
                    handler.postDelayed(this, 200)
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

    private fun updateButtonState(book: Book?) {
        val root = binding.detailRootLayout
        TransitionManager.beginDelayedTransition(root, Fade())

        if (isBookDownloaded) {
            binding.layoutTwoButtons.visibility = View.GONE
            binding.progressBarDownload.visibility = View.GONE
            binding.btnBacaSingle.visibility = View.VISIBLE
            binding.btnBacaSingle.isEnabled = isBookDownloaded

        } else {
            binding.btnBacaSingle.visibility = View.GONE
            binding.progressBarDownload.visibility = View.GONE
            binding.layoutTwoButtons.visibility = View.VISIBLE
            binding.btnDownload.isEnabled = true
            binding.btnBacaPratinjau.isEnabled = !isBookDownloaded
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun onStart() {
        super.onStart()
        updateButtonState(book)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null) // Hapus callback untuk menghindari memory leak
    }

    companion object {
        const val EXTRA_BOOK_ID = "extra_book_id"
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("is_downloaded", isBookDownloaded)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        isBookDownloaded = savedInstanceState.getBoolean("is_downloaded", false)
    }
}