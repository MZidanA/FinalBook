package com.insfinal.bookdforall.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
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
import com.bumptech.glide.Glide
import com.insfinal.bookdforall.R
import com.insfinal.bookdforall.databinding.ActivityBookDetailBinding
import com.insfinal.bookdforall.model.Book
import com.insfinal.bookdforall.model.BookmarkInput
import com.insfinal.bookdforall.model.ReviewInput
import com.insfinal.bookdforall.network.RetrofitInstance
import com.insfinal.bookdforall.data.BookData // <-- Perbarui import ini
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class BookDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookDetailBinding
    private var isBookDownloaded: Boolean = false
    private val handler = Handler(Looper.getMainLooper())
    private var downloadProgress: Int = 0
    private var currentBook: Book? = null

    private lateinit var openPdfLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        openPdfLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                promptForBookmark()
            }
        }

        currentBook = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_BOOK, Book::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_BOOK)
        }

        if (currentBook != null) {
            displayBookDetails(currentBook!!)
            isBookDownloaded = checkBookDownloadStatus(currentBook!!)
            updateButtonState(currentBook!!)
        } else {
            Toast.makeText(this, "Failed to load book details.", Toast.LENGTH_SHORT).show()
            finish()
        }

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnDownload.setOnClickListener {
            val bookToDownload = currentBook
            if (bookToDownload != null) {
                val assetFileName = BookData.getAssetFileName(bookToDownload.bookId) // <-- Gunakan BookData
                if (assetFileName != null) {
                    simulateDownload(bookToDownload, assetFileName)
                } else {
                    Toast.makeText(this, "Aset buku tidak ditemukan untuk diunduh.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnBacaPratinjau.setOnClickListener {
            Toast.makeText(this, "Opening Preview for: ${currentBook?.judul}", Toast.LENGTH_SHORT).show()
            currentBook?.let { openBookPdf(it) }
        }

        binding.btnBacaSingle.setOnClickListener {
            Toast.makeText(this, "Opening book: ${currentBook?.judul}", Toast.LENGTH_SHORT).show()
            currentBook?.let { openBookPdf(it) }
        }

        binding.btnWriteReview.setOnClickListener {
            currentBook?.let { book ->
                val sharedPrefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                val userId = sharedPrefs.getInt("user_id", -1)

                if (userId != -1) {
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            val tanggalReview = sdf.format(Date())
                            val reviewInput = ReviewInput(userId, book.bookId, 5, "Buku yang luar biasa!", tanggalReview)

                            val response = RetrofitInstance.api.createReview(reviewInput)
                            withContext(Dispatchers.Main) {
                                if (response.isSuccessful) {
                                    Toast.makeText(this@BookDetailActivity, "Ulasan berhasil dikirim!", Toast.LENGTH_SHORT).show()
                                } else {
                                    val errorBody = response.errorBody()?.string()
                                    Toast.makeText(this@BookDetailActivity, "Gagal mengirim ulasan: ${errorBody ?: response.message()}", Toast.LENGTH_LONG).show()
                                }
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(this@BookDetailActivity, "Error mengirim ulasan: ${e.message}", Toast.LENGTH_LONG).show()
                                e.printStackTrace()
                            }
                        }
                    }
                } else {
                    Toast.makeText(this, "Silakan login untuk menulis ulasan.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun displayBookDetails(book: Book) {
        Glide.with(this)
            .load(book.coverImageUrl)
            .placeholder(R.drawable.placeholder_book_cover)
            .error(R.drawable.placeholder_book_cover)
            .into(binding.ivBookCoverDetail)

        binding.tvBookTitleDetail.text = book.judul
        binding.tvBookAuthorDetail.text = book.penulis
        binding.tvBookDescription.text = book.deskripsi

        binding.ratingBar.rating = 4.8f
        binding.tvRatingCount.text = "(230)"

        binding.tvInfoLanguage.text = "Indonesia"
        binding.tvInfoReleaseDate.text = "8 September 1999"
        binding.tvInfoPublisher.text = book.publisherId?.toString() ?: "N/A"
        binding.tvInfoAuthor.text = book.penulis
        binding.tvInfoPages.text = "544 Halaman"
        binding.tvInfoFormat.text = book.format
        binding.tvInfoCategory.text = book.kategori
    }

    private fun simulateDownload(book: Book, assetFileName: String) {
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
                    isBookDownloaded = true
                    copyPdfFromAssetsToInternalStorage(book, assetFileName)
                    saveBookDownloadStatus(book.bookId, true)
                    updateButtonState(book)
                    Toast.makeText(this@BookDetailActivity, "${book.judul} successfully downloaded!", Toast.LENGTH_SHORT).show()
                }
            }
        }
        handler.post(runnable)
    }

    private fun updateButtonState(book: Book?) {
        val root = binding.detailRootLayout
        TransitionManager.beginDelayedTransition(root, Fade())

        val hasAssetFile = book?.let { BookData.getAssetFileName(it.bookId) } != null // <-- Gunakan BookData

        if (isBookDownloaded) {
            binding.layoutTwoButtons.visibility = View.GONE
            binding.progressBarDownload.visibility = View.GONE
            binding.btnBacaSingle.visibility = View.VISIBLE
        } else {
            binding.btnBacaSingle.visibility = View.GONE
            binding.progressBarDownload.visibility = View.GONE
            binding.layoutTwoButtons.visibility = View.VISIBLE
            binding.btnDownload.isEnabled = hasAssetFile
            binding.btnBacaPratinjau.isEnabled = hasAssetFile
        }
    }

    private fun copyPdfFromAssetsToInternalStorage(book: Book, assetFileName: String) {
        val internalFileName = "${book.bookId}.pdf"
        val file = File(filesDir, internalFileName)

        if (file.exists()) {
            return
        }

        try {
            assets.open(assetFileName).use { inputStream ->
                FileOutputStream(file).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            Toast.makeText(this, "PDF copied to internal storage: $internalFileName", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to copy PDF from assets: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun isBookPdfDownloaded(book: Book): Boolean {
        val fileName = "${book.bookId}.pdf"
        val file = File(filesDir, fileName)
        return file.exists()
    }

    private fun openBookPdf(book: Book) {
        val fileName = "${book.bookId}.pdf"
        val pdfFile = File(filesDir, fileName)

        if (pdfFile.exists()) {
            val uri = androidx.core.content.FileProvider.getUriForFile(
                this,
                applicationContext.packageName + ".fileprovider",
                pdfFile
            )

            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(uri, "application/pdf")
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NO_HISTORY

            val packageManager = applicationContext.packageManager
            val activities = packageManager.queryIntentActivities(intent, 0)
            val isIntentSafe = activities.isNotEmpty()

            if (isIntentSafe) {
                val lastPage = getBookmark(book.bookId)
                if (lastPage != -1) {
                    Toast.makeText(this, "Loading from bookmark for ${book.judul} at page ${lastPage} (simulated).", Toast.LENGTH_LONG).show()
                }

                val chooser = Intent.createChooser(intent, "Open PDF with...")
                openPdfLauncher.launch(chooser)
            } else {
                Toast.makeText(this, "No application found to open PDF.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "File is not downloaded. Please download first.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun promptForBookmark() {
        val bookId = currentBook?.bookId ?: return
        if (!isBookDownloaded) return

        AlertDialog.Builder(this)
            .setTitle("Simpan Bookmark?")
            .setMessage("Apakah Anda ingin menyimpan posisi baca terakhir untuk buku ini?")
            .setPositiveButton("Iya") { dialog, which ->
                val sharedPrefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                val userId = sharedPrefs.getInt("user_id", -1)

                if (userId != -1) {
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            val tanggal = sdf.format(Date())
                            val bookmarkInput = BookmarkInput(userId, bookId, 0, tanggal)

                            val response = RetrofitInstance.api.createBookmark(bookmarkInput)
                            withContext(Dispatchers.Main) {
                                if (response.isSuccessful) {
                                    saveBookmark(bookId, 0)
                                    Toast.makeText(this@BookDetailActivity, "Bookmark disimpan!", Toast.LENGTH_SHORT).show()
                                } else {
                                    val errorBody = response.errorBody()?.string()
                                    Toast.makeText(this@BookDetailActivity, "Gagal menyimpan bookmark: ${errorBody ?: response.message()}", Toast.LENGTH_LONG).show()
                                }
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(this@BookDetailActivity, "Error menyimpan bookmark: ${e.message}", Toast.LENGTH_LONG).show()
                                e.printStackTrace()
                            }
                        }
                    }
                } else {
                    Toast.makeText(this, "User ID tidak ditemukan. Tidak dapat menyimpan bookmark.", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Tidak") { dialog, which ->
                // Tidak melakukan apa-apa
            }
            .show()
    }

    private fun saveBookDownloadStatus(bookId: Int, downloaded: Boolean) {
        val sharedPrefs = getSharedPreferences("download_status", Context.MODE_PRIVATE)
        sharedPrefs.edit().putBoolean("book_${bookId}_downloaded", downloaded).apply()
    }

    private fun checkBookDownloadStatus(book: Book): Boolean {
        val sharedPrefs = getSharedPreferences("download_status", Context.MODE_PRIVATE)
        return sharedPrefs.getBoolean("book_${book.bookId}_downloaded", false) && isBookPdfDownloaded(book)
    }

    private fun saveBookmark(bookId: Int, page: Int) {
        val sharedPrefs = getSharedPreferences("bookmarks", Context.MODE_PRIVATE)
        sharedPrefs.edit().putInt("bookmark_book_${bookId}", page).apply()
    }

    private fun getBookmark(bookId: Int): Int {
        val sharedPrefs = getSharedPreferences("bookmarks", Context.MODE_PRIVATE)
        return sharedPrefs.getInt("bookmark_book_${bookId}", -1)
    }

    override fun onStart() {
        super.onStart()
        currentBook?.let {
            isBookDownloaded = checkBookDownloadStatus(it)
            updateButtonState(it)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }

    companion object {
        const val EXTRA_BOOK = "extra_book"
    }
}