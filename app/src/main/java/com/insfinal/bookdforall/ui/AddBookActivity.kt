// AddBookActivity.kt
package com.insfinal.bookdforall.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.insfinal.bookdforall.R
import com.insfinal.bookdforall.databinding.ActivityAddBookBinding
import com.insfinal.bookdforall.model.CreateBookRequest
import com.insfinal.bookdforall.repository.BookRepository
import com.insfinal.bookdforall.utils.FileUtils
import kotlinx.coroutines.launch
import java.io.File

class AddBookActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddBookBinding
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
        binding = ActivityAddBookBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnUploadCover.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.selectBookFileButton.setOnClickListener { // PERBAIKAN: selectBookFileButton
            pickPdfLauncher.launch("application/pdf")
        }

        binding.btnAddBook.setOnClickListener {
            addBook()
        }
    }

    private fun addBook() {
        val judul = binding.etJudul.text.toString().trim()
        val penulis = binding.etPenulis.text.toString().trim()
        val deskripsi = binding.etDeskripsi.text.toString().trim()
        val kategori = binding.etKategori.text.toString().trim()
        val harga = binding.etHarga.text.toString().trim().toDoubleOrNull()
        val format = binding.etFormat.text.toString().trim()
        // releaseDate dan totalPages tidak ada di CreateBookRequest
        // val releaseDate = binding.etReleaseDate.text.toString().trim()
        // val totalPages = binding.etTotalPages.text.toString().trim().toIntOrNull()


        if (judul.isEmpty() || penulis.isEmpty() || deskripsi.isEmpty() || kategori.isEmpty() ||
            harga == null || format.isEmpty() || selectedImageUri == null || selectedPdfUri == null) {
            Toast.makeText(this, "Semua kolom wajib diisi, termasuk gambar cover dan file PDF!", Toast.LENGTH_SHORT).show()
            return
        }

        val createBookRequest = CreateBookRequest(
            judul = judul,
            penulis = penulis,
            deskripsi = deskripsi,
            kategori = kategori,
            harga = harga,
            format = format,
            coverImageUrl = selectedImageUri?.toString() ?: "", // Gunakan URI yang dipilih
            publisherId = 1,
            contentProviderId = 1
        )

        lifecycleScope.launch {
            try {
                // Untuk upload file, kita perlu menggunakan BookRepository().uploadBook()
                // Ini akan mengasumsikan API akan mengaitkan file yang diupload
                // dengan buku yang baru dibuat (misal via ID dari metadataResponse jika ada)
                val uploadResponse = BookRepository().uploadBook(
                    this@AddBookActivity,
                    judul,
                    penulis,
                    deskripsi,
                    selectedImageUri,
                    selectedPdfUri!!
                )

                if (uploadResponse.isSuccessful) {
                    Toast.makeText(this@AddBookActivity, "Buku berhasil ditambahkan dan file diupload!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    val errorBody = uploadResponse.errorBody()?.string()
                    Toast.makeText(this@AddBookActivity, "Gagal upload file: ${errorBody ?: uploadResponse.message()}", Toast.LENGTH_LONG).show()
                    Log.e("AddBookActivity", "Failed to upload file: ${uploadResponse.code()} - ${errorBody ?: uploadResponse.message()}")
                }

            } catch (e: Exception) {
                Toast.makeText(this@AddBookActivity, "Error menambahkan buku: ${e.message}", Toast.LENGTH_LONG).show()
                Log.e("AddBookActivity", "Exception adding book: ${e.message}", e)
            }
        }
    }
}