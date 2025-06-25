// EditBookActivity.kt
package com.insfinal.bookdforall.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.insfinal.bookdforall.R
import com.insfinal.bookdforall.databinding.ActivityEditBookBinding
import com.insfinal.bookdforall.model.Book
import com.insfinal.bookdforall.model.CreateBookRequest
import com.insfinal.bookdforall.repository.BookRepository
import com.insfinal.bookdforall.utils.BookAssetPaths
import com.insfinal.bookdforall.utils.FileUtils
import kotlinx.coroutines.launch
import java.io.File

class EditBookActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditBookBinding
    private var bookId: Int = -1
    private var currentBook: Book? = null
    private var selectedImageUri: Uri? = null
    private var selectedPdfUri: Uri? = null

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            binding.ivCoverPreview.setImageURI(it)
        }
    }

    private val pickPdfLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedPdfUri = it
            // PERBAIKAN: fileNameText dan getFileName
            binding.fileNameText.text = FileUtils.getFileName(this, uri) ?: uri.lastPathSegment ?: "File dipilih"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBookBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        bookId = intent.getIntExtra(EXTRA_BOOK_ID, -1)
        if (bookId != -1) {
            fetchBookDetails(bookId)
        } else {
            Toast.makeText(this, "ID Buku tidak valid untuk diedit.", Toast.LENGTH_SHORT).show()
            finish()
        }

        binding.btnUploadCover.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.selectBookFileButton.setOnClickListener { // PERBAIKAN: selectBookFileButton
            pickPdfLauncher.launch("application/pdf")
        }

        binding.btnSaveBook.setOnClickListener {
            saveBookChanges()
        }

        binding.btnDeleteBook.setOnClickListener {
            showDeleteConfirmationDialog()
        }
    }

    private fun fetchBookDetails(id: Int) {
        lifecycleScope.launch {
            try {
                val response = BookRepository().getBookById(id)
                if (response.isSuccessful) {
                    response.body()?.let { book ->
                        currentBook = book
                        displayBookDetails(book)
                    }
                } else {
                    Toast.makeText(this@EditBookActivity, "Gagal memuat detail buku.", Toast.LENGTH_SHORT).show()
                    Log.e("EditBookActivity", "Failed to fetch book details: ${response.code()}")
                    finish()
                }
            } catch (e: Exception) {
                Toast.makeText(this@EditBookActivity, "Error memuat detail buku: ${e.message}", Toast.LENGTH_LONG).show()
                Log.e("EditBookActivity", "Exception fetching book details: ${e.message}", e)
                finish()
            }
        }
    }

    private fun displayBookDetails(book: Book) {
        binding.etJudul.setText(book.judul)
        binding.etPenulis.setText(book.penulis)
        binding.etDeskripsi.setText(book.deskripsi)
        binding.etKategori.setText(book.kategori)
        binding.etHarga.setText(book.harga.toString())
        binding.etFormat.setText(book.format)
        binding.etReleaseDate.setText(book.releaseDate)
        binding.etTotalPages.setText(book.totalPages.toString())

        // Display current file name for PDF
        val downloadsDir = getExternalFilesDir(null)?.let { File(it, "books_downloaded") }
        val currentPdfFileName = BookAssetPaths.getPdfFileName(book.bookId) // Fallback if no specific file stored
        val localPdfFile = downloadsDir?.let { File(it, currentPdfFileName ?: "") }

        if (localPdfFile?.exists() == true && localPdfFile.length() > 0) {
            binding.fileNameText.text = localPdfFile.name
            selectedPdfUri = Uri.fromFile(localPdfFile) // Set current PDF as selected
        } else if (!book.pdfFileName.isNullOrEmpty()) { // Assuming Book model has a pdfFileName for current file if not local
            binding.fileNameText.text = book.pdfFileName // Display name from book model
            // selectedPdfUri might not be set for external URLs
        } else {
            binding.fileNameText.text = "Belum ada file dipilih"
            selectedPdfUri = null
        }

        // Load cover image
        if (!book.coverImageUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(book.coverImageUrl)
                .placeholder(R.drawable.placeholder_book_cover)
                .error(R.drawable.error_book_cover)
                .into(binding.ivCoverPreview)
        }
    }

    private fun saveBookChanges() {
        val updatedJudul = binding.etJudul.text.toString().trim()
        val updatedPenulis = binding.etPenulis.text.toString().trim()
        val updatedDeskripsi = binding.etDeskripsi.text.toString().trim()
        val updatedKategori = binding.etKategori.text.toString().trim()
        val updatedHarga = binding.etHarga.text.toString().trim().toDoubleOrNull()
        val updatedFormat = binding.etFormat.text.toString().trim()
        // releaseDate dan totalPages tidak lagi ada di CreateBookRequest
        // val updatedReleaseDate = binding.etReleaseDate.text.toString().trim()
        // val updatedTotalPages = binding.etTotalPages.text.toString().trim().toIntOrNull()

        if (updatedJudul.isEmpty() || updatedPenulis.isEmpty() || updatedDeskripsi.isEmpty() || updatedKategori.isEmpty() ||
            updatedHarga == null || updatedFormat.isEmpty()) { // Hapus cek releaseDate/totalPages
            Toast.makeText(this, "Semua kolom wajib diisi!", Toast.LENGTH_SHORT).show()
            return
        }

        // --- PERUBAHAN: Membuat CreateBookRequest tanpa rating, ratingCount, releaseDate, totalPages ---
        val updatedBookRequest = CreateBookRequest(
            judul = updatedJudul,
            penulis = updatedPenulis,
            deskripsi = updatedDeskripsi,
            kategori = updatedKategori,
            harga = updatedHarga,
            format = updatedFormat,
            coverImageUrl = selectedImageUri?.toString() ?: (currentBook?.coverImageUrl ?: ""),
            publisherId = currentBook?.publisherId ?: 1,
            contentProviderId = currentBook?.contentProviderId ?: 1
        )

        lifecycleScope.launch {
            try {
                // Panggil BookRepository().update() untuk metadata
                val updateMetadataResponse = BookRepository().update(bookId, updatedBookRequest)
                if (!updateMetadataResponse.isSuccessful) {
                    val errorBody = updateMetadataResponse.errorBody()?.string()
                    Toast.makeText(this@EditBookActivity, "Gagal menyimpan perubahan metadata: ${errorBody ?: updateMetadataResponse.message()}", Toast.LENGTH_LONG).show()
                    Log.e("EditBookActivity", "Failed to save metadata: ${updateMetadataResponse.code()} - ${errorBody ?: updateMetadataResponse.message()}")
                    return@launch
                }

                // Jika ada file cover atau PDF baru yang dipilih, lakukan upload terpisah
                if (selectedImageUri != null || selectedPdfUri != null) {
                    val uploadResponse = BookRepository().uploadBook(
                        this@EditBookActivity,
                        updatedJudul, // Gunakan judul yang diupdate
                        updatedPenulis, // Gunakan penulis yang diupdate
                        updatedDeskripsi, // Gunakan deskripsi yang diupdate
                        selectedImageUri, // Gunakan URI gambar baru jika ada
                        selectedPdfUri // Gunakan URI PDF baru jika ada
                    )

                    if (!uploadResponse.isSuccessful) {
                        val errorBody = uploadResponse.errorBody()?.string()
                        Toast.makeText(this@EditBookActivity, "Metadata tersimpan, tapi gagal upload file: ${errorBody ?: uploadResponse.message()}", Toast.LENGTH_LONG).show()
                        Log.e("EditBookActivity", "Failed to upload file during edit: ${uploadResponse.code()} - ${errorBody ?: uploadResponse.message()}")
                        return@launch
                    }
                }

                Toast.makeText(this@EditBookActivity, "Perubahan berhasil disimpan!", Toast.LENGTH_SHORT).show()
                finish()

            } catch (e: Exception) {
                Toast.makeText(this@EditBookActivity, "Error menyimpan perubahan: ${e.message}", Toast.LENGTH_LONG).show()
                Log.e("EditBookActivity", "Exception saving changes: ${e.message}", e)
            }
        }
    }


    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Hapus Buku")
            .setMessage("Apakah Anda yakin ingin menghapus buku '${currentBook?.judul}'?")
            .setPositiveButton("Hapus") { dialog, which ->
                deleteBook()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun deleteBook() {
        lifecycleScope.launch {
            try {
                val response = BookRepository().delete(bookId)
                if (response.isSuccessful) {
                    Toast.makeText(this@EditBookActivity, "Buku berhasil dihapus!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    val errorBody = response.errorBody()?.string()
                    Toast.makeText(this@EditBookActivity, "Gagal menghapus buku: ${errorBody ?: response.message()}", Toast.LENGTH_LONG).show()
                    Log.e("EditBookActivity", "Failed to delete book: ${response.code()} - ${errorBody ?: response.message()}")
                }
            } catch (e: Exception) {
                Toast.makeText(this@EditBookActivity, "Error menghapus buku: ${e.message}", Toast.LENGTH_LONG).show()
                Log.e("EditBookActivity", "Exception deleting book: ${e.message}", e)
            }
        }
    }

    companion object {
        const val EXTRA_BOOK_ID = "extra_book_id"
    }
}