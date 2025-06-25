package com.insfinal.bookdforall.ui.admin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.booksforall.databinding.ActivityEditBookBinding


class EditBookActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditBookBinding
    private val viewModel: BookManageViewModel by viewModels()
    private var selectedCoverUri: Uri? = null
    private var selectedPdfUri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBookBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupFilePicker()
        setupCoverPicker()
        setupSubmitButton()

        lifecycleScope.launchWhenStarted {
            viewModel.eventSuccess.collect {
                Toast.makeText(this@EditBookActivity, it, Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.eventError.collect {
                Toast.makeText(this@EditBookActivity, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupCoverPicker() {
        binding.editBookCover.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply { type = "image/*" }
            pickImageLauncher.launch(intent)
        }
    }

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            val uri = it.data?.data
            uri?.let { selectedUri ->
                selectedCoverUri = selectedUri
                binding.editBookCover.setImageURI(selectedUri)
            }
        }
    }

    private fun setupFilePicker() {
        binding.selectBookFileButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply { type = "application/pdf" }
            filePickerLauncher.launch(intent)
        }
    }

    private val filePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            val uri = it.data?.data
            uri?.let { selectedFile ->
                selectedPdfUri = selectedFile
                binding.fileNameText.text = selectedFile.lastPathSegment
            }
        }
    }

    private fun setupSubmitButton() {
        binding.tombolSimpanEditBuku.setOnClickListener {
            val title = binding.editBookTitle.text.toString()
            val author = binding.editBookAuthor.text.toString()
            val description = binding.editBookDescription.text.toString()

            if (title.isBlank() || author.isBlank() || description.isBlank()) {
                Toast.makeText(this, "Mohon lengkapi semua bidang", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.updateBook(
                context = this,
                title = title,
                author = author,
                description = description,
                coverUri = selectedCoverUri,
                pdfUri = selectedPdfUri
            )
        }
    }
}