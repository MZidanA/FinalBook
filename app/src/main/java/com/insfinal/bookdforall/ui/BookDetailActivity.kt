package com.insfinal.bookdforall.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.transition.Fade
import android.transition.TransitionManager
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat // Sudah ada
import androidx.core.os.BundleCompat // <--- TAMBAHKAN IMPORT INI
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.insfinal.bookdforall.databinding.ActivityBookDetailBinding
import com.insfinal.bookdforall.model.Book
import com.insfinal.bookdforall.repository.BookRepository
import com.insfinal.bookdforall.utils.BookAssetPaths
import com.insfinal.bookdforall.utils.PrefManager
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import android.util.Log

class BookDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookDetailBinding
    private var isBookDownloaded: Boolean = false
    private val handler = Handler(Looper.getMainLooper())
    private var downloadProgress: Int = 0
    private var book: Book? = null

    private lateinit var openPdfViewerLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        openPdfViewerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                Log.d("BookDetailActivity", "PdfViewerActivity finished. Bookmark saved (handled by PdfViewerActivity).")
            }
            book?.let { currentBook ->
                isBookDownloaded = isBookDownloaded(currentBook)
                updateButtonState(currentBook)
            }
        }

        val bookId = intent.getIntExtra(EXTRA_BOOK_ID, -1)
        if (bookId != -1) {
            // Ini sudah benar menggunakan IntentCompat.getParcelableExtra
            book = IntentCompat.getParcelableExtra(intent, "book_object", Book::class.java)
            if (book != null) {
                book = book!!.copy(
                    pdfFileName = BookAssetPaths.getPdfFileName(book!!.bookId),
                    coverImageFileName = BookAssetPaths.getCoverImageFileName(book!!.bookId)
                )
                displayBookDetails(book!!)
                isBookDownloaded = isBookDownloaded(book!!)
                updateButtonState(book!!)
            } else {
                fetchBookById(bookId)
            }
        } else {
            showError("ID buku tidak valid.")
        }

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnDownload.setOnClickListener {
            book?.let {
                val downloadsDir = File(getExternalFilesDir(null), "books_downloaded")
                val fileName = it.pdfFileName
                if (fileName != null) {
                    val localFile = File(downloadsDir, fileName)
                    if (localFile.exists() && localFile.length() > 0) {
                        Toast.makeText(this, "${it.judul} sudah diunduh.", Toast.LENGTH_SHORT).show()
                        isBookDownloaded = true
                        updateButtonState(it)
                    } else {
                        simulateAndCopyDownload(it)
                    }
                } else {
                    Toast.makeText(this, "File PDF untuk buku ini tidak terdefinisi.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnBacaPratinjau.setOnClickListener {
            Toast.makeText(this, "Membuka Pratinjau untuk: ${book?.judul}", Toast.LENGTH_SHORT).show()
            book?.let { openBookPdf(it) }
        }

        binding.btnBacaSingle.setOnClickListener {
            book?.let { currentBook ->
                val downloadsDir = File(getExternalFilesDir(null), "books_downloaded")
                val fileName = currentBook.pdfFileName
                if (fileName != null) {
                    val file = File(downloadsDir, fileName)

                    if (file.exists() && file.length() > 0) {
                        val intent = Intent(this, PdfViewerActivity::class.java).apply {
                            putExtra(PdfViewerActivity.EXTRA_BOOK_ID, currentBook.bookId)
                            putExtra(PdfViewerActivity.EXTRA_BOOK_FILENAME, fileName)
                            putExtra(PdfViewerActivity.EXTRA_BOOK_TITLE, currentBook.judul)
                        }
                        openPdfViewerLauncher.launch(intent)
                    } else {
                        val errorMessage = if (file.exists() && file.length() == 0L) {
                            "File buku ditemukan tapi kosong. Harap unduh ulang."
                        } else {
                            "File buku tidak ditemukan. Harap unduh terlebih dahulu."
                        }
                        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                        isBookDownloaded = false
                        updateButtonState(currentBook)
                    }
                } else {
                    Toast.makeText(this, "File PDF untuk buku ini tidak terdefinisi.", Toast.LENGTH_SHORT).show()
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
                        book = result.copy(
                            pdfFileName = BookAssetPaths.getPdfFileName(result.bookId),
                            coverImageFileName = BookAssetPaths.getCoverImageFileName(result.bookId)
                        )
                        displayBookDetails(book!!)
                        isBookDownloaded = isBookDownloaded(book!!)
                        updateButtonState(book!!)
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
        val coverFileName = book.coverImageFileName
        if (coverFileName != null) {
            val assetPath = BookAssetPaths.ASSETS_ROOT + BookAssetPaths.ASSETS_IMG_PATH + coverFileName
            Glide.with(this)
                .load(assetPath)
                .placeholder(com.insfinal.bookdforall.R.drawable.placeholder_book_cover)
                .error(com.insfinal.bookdforall.R.drawable.error_book_cover)
                .into(binding.ivBookCoverDetail)
        } else {
            Glide.with(this)
                .load(com.insfinal.bookdforall.R.drawable.placeholder_book_cover)
                .into(binding.ivBookCoverDetail)
        }

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

    private fun isBookDownloaded(book: Book): Boolean {
        val downloadsDir = File(getExternalFilesDir(null), "books_downloaded")
        val fileName = book.pdfFileName
        return if (fileName != null) {
            File(downloadsDir, fileName).exists() && File(downloadsDir, fileName).length() > 0
        } else {
            false
        }
    }

    private fun simulateAndCopyDownload(book: Book) {
        val pdfAssetFileName = book.pdfFileName
        if (pdfAssetFileName == null) {
            Toast.makeText(this, "File PDF untuk buku ini tidak tersedia.", Toast.LENGTH_SHORT).show()
            return
        }

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
                    copyAssetPdfToInternalStorage(book) { success ->
                        if (success) {
                            isBookDownloaded = true
                            PrefManager.addDownloadedBook(this@BookDetailActivity, book.bookId, pdfAssetFileName, book.judul, book.penulis)
                            updateButtonState(book)
                            Toast.makeText(this@BookDetailActivity, "${book.judul} berhasil diunduh!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@BookDetailActivity, "Gagal menyalin buku ${book.judul}.", Toast.LENGTH_LONG).show()
                            isBookDownloaded = false
                            updateButtonState(book)
                        }
                    }
                }
            }
        }
        handler.post(runnable)
    }

    private fun copyAssetPdfToInternalStorage(book: Book, onComplete: (Boolean) -> Unit) {
        val downloadsDir = File(getExternalFilesDir(null), "books_downloaded")
        if (!downloadsDir.exists()) {
            downloadsDir.mkdirs()
            Log.d("CopyPdf", "Created downloads directory: ${downloadsDir.absolutePath}")
        }

        val pdfAssetFileName = book.pdfFileName
        if (pdfAssetFileName == null) {
            Log.e("CopyPdf", "PDF asset file name is null for book: ${book.judul}")
            onComplete(false)
            return
        }

        val outputFile = File(downloadsDir, pdfAssetFileName)
        Log.d("CopyPdf", "Attempting to copy from assets/${pdfAssetFileName} to ${outputFile.absolutePath}")

        if (outputFile.exists() && outputFile.length() > 0) {
            Log.d("CopyPdf", "File already exists and is not empty: ${outputFile.absolutePath}")
            onComplete(true)
            return
        }

        try {
            assets.open(pdfAssetFileName).use { inputStream ->
                FileOutputStream(outputFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            Log.d("CopyPdf", "Successfully copied ${pdfAssetFileName}")
            onComplete(true)
        } catch (e: IOException) {
            Log.e("CopyPdf", "Error copying file ${pdfAssetFileName}: ${e.message}", e)
            Toast.makeText(this, "Error menyalin file: ${e.message}", Toast.LENGTH_LONG).show()
            onComplete(false)
        }
    }

    private fun updateButtonState(book: Book?) {
        val root = binding.detailRootLayout
        TransitionManager.beginDelayedTransition(root, Fade())

        val hasAssetFile = book?.let { BookAssetPaths.getPdfFileName(it.bookId) } != null
        isBookDownloaded = isBookDownloaded(book ?: return) // Pastikan book tidak null di sini

        if (isBookDownloaded) {
            binding.layoutTwoButtons.visibility = View.GONE
            binding.progressBarDownload.visibility = View.GONE
            binding.btnBacaSingle.visibility = View.VISIBLE
            binding.btnBacaSingle.isEnabled = true
        } else {
            binding.btnBacaSingle.visibility = View.GONE
            binding.progressBarDownload.visibility = View.GONE
            binding.layoutTwoButtons.visibility = View.VISIBLE
            binding.btnDownload.isEnabled = hasAssetFile
            binding.btnBacaPratinjau.isEnabled = hasAssetFile
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun onStart() {
        super.onStart()
        book?.let {
            isBookDownloaded = isBookDownloaded(it)
            updateButtonState(it)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }

    companion object {
        const val EXTRA_BOOK_ID = "extra_book_id"
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("is_downloaded", isBookDownloaded)
        // Gunakan BundleCompat.putParcelable untuk menghindari deprecation warning
        outState.putParcelable("current_book", book)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        isBookDownloaded = savedInstanceState.getBoolean("is_downloaded", false)
        // Gunakan BundleCompat.getParcelable untuk menghindari deprecation warning
        book = BundleCompat.getParcelable(savedInstanceState, "current_book", Book::class.java) // <--- PERBAIKAN DI SINI
        // updateButtonState(book) akan dipanggil di onStart setelah ini
    }

    private fun openBookPdf(book: Book) {
        val downloadsDir = File(getExternalFilesDir(null), "books_downloaded")
        val assetFileName = BookAssetPaths.getPdfFileName(book.bookId)

        if (assetFileName == null) {
            Toast.makeText(this, "Aset PDF buku tidak ditemukan.", Toast.LENGTH_SHORT).show()
            Log.e("BookDetailActivity", "PDF asset filename is null for book ID: ${book.bookId}")
            return
        }

        val pdfFile = File(downloadsDir, assetFileName)

        if (pdfFile.exists() && pdfFile.length() > 0) {
            val intent = Intent(this, PdfViewerActivity::class.java).apply {
                putExtra(PdfViewerActivity.EXTRA_BOOK_ID, book.bookId)
                putExtra(PdfViewerActivity.EXTRA_BOOK_FILENAME, assetFileName)
                putExtra(PdfViewerActivity.EXTRA_BOOK_TITLE, book.judul)
            }
            openPdfViewerLauncher.launch(intent)
        } else {
            Toast.makeText(this, "File is not downloaded or is empty. Please download first.", Toast.LENGTH_SHORT).show()
            Log.e("BookDetailActivity", "PDF file not found or is empty: ${pdfFile.absolutePath}")
        }
    }
}