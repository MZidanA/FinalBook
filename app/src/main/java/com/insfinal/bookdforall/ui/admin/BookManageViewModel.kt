package com.insfinal.bookdforall.ui.admin

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.insfinal.bookdforall.model.Book
import com.insfinal.bookdforall.network.RetrofitInstance
import com.insfinal.bookdforall.repository.BookRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class BookManageViewModel : ViewModel() {

    private val _books = MutableLiveData<List<Book>>()
    val books: LiveData<List<Book>> = _books

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private var originalBooks: List<Book> = emptyList()

    private val _eventSuccess = MutableSharedFlow<String>()
    val eventSuccess = _eventSuccess.asSharedFlow()

    private val _eventError = MutableSharedFlow<String>()
    val eventError = _eventError.asSharedFlow()

    private val repository = BookRepository()

    fun loadBooks() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getBooks()
                if (response.isSuccessful) {
                    originalBooks = response.body() ?: emptyList()
                    _books.value = originalBooks
                } else {
                    _errorMessage.value = "Gagal memuat buku: ${response.code()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createBook(
        context: Context,
        title: String,
        author: String,
        description: String,
        coverUri: Uri?,
        pdfUri: Uri?
    ) {
        viewModelScope.launch {
            try {
                repository.uploadBook(context, title, author, description, coverUri, pdfUri)
                _eventSuccess.emit("Buku berhasil ditambahkan")
            } catch (e: Exception) {
                _eventError.emit("Gagal menambahkan buku: ${e.message}")
            }
        }
    }

    fun deleteBook(id: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.deleteBook(id)
                if (response.isSuccessful) {
                    _books.value = _books.value?.filterNot { it.bookId == id }
                } else {
                    _errorMessage.value = "Gagal menghapus buku: ${response.code()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.localizedMessage}"
            }
        }
    }

    fun updateBook(
        context: Context,
        title: String,
        author: String,
        description: String,
        coverUri: Uri?,
        pdfUri: Uri?
    ) {
        viewModelScope.launch {
            try {
                repository.uploadBook(context, title, author, description, coverUri, pdfUri)
                _eventSuccess.emit("Buku berhasil diperbarui")
            } catch (e: Exception) {
                _eventError.emit("Gagal memperbarui buku: ${e.message}")
            }
        }
    }

    fun searchBooks(query: String) {
        val filtered = originalBooks.filter {
            it.judul.contains(query, ignoreCase = true) ||
                    it.penulis.contains(query, ignoreCase = true)
        }
        _books.value = filtered
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    fun resetSearch() {
        _books.value = originalBooks
    }
}