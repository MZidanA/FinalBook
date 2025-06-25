package com.insfinal.bookdforall.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.insfinal.bookdforall.model.Book
import com.insfinal.bookdforall.repository.BookRepository
import kotlinx.coroutines.launch
import java.util.Locale

class HomeViewModel : ViewModel() {

    private val bookRepository = BookRepository()

    private var allBooksLoaded: List<Book> = emptyList()

    private val _trendingBooks = MutableLiveData<List<Book>>()
    val trendingBooks: LiveData<List<Book>> get() = _trendingBooks

    private val _fantasyBooks = MutableLiveData<List<Book>>()
    val fantasyBooks: LiveData<List<Book>> get() = _fantasyBooks

    private val _fictionBooks = MutableLiveData<List<Book>>()
    val fictionBooks: LiveData<List<Book>> get() = _fictionBooks

    private val _selfHelpBooks = MutableLiveData<List<Book>>()
    val selfHelpBooks: LiveData<List<Book>> get() = _selfHelpBooks

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    init {
        loadBooks()
    }

    fun loadBooks() {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val fetchedBooks = bookRepository.getLocalDummyBooks()
                allBooksLoaded = fetchedBooks

                _trendingBooks.value = allBooksLoaded
                _fantasyBooks.value = allBooksLoaded.filter { it.kategori == "Fantasy" }
                _fictionBooks.value = allBooksLoaded.filter { it.kategori == "Fiction" }
                _selfHelpBooks.value = allBooksLoaded.filter { it.kategori == "Self-Help" }

                Log.d("HomeViewModel", "Loaded ${allBooksLoaded.size} total dummy books.")
                Log.d("HomeViewModel", "Fantasy Books: ${_fantasyBooks.value?.size}")
                Log.d("HomeViewModel", "Fiction Books: ${_fictionBooks.value?.size}")
                Log.d("HomeViewModel", "Self-Help Books: ${_selfHelpBooks.value?.size}")

            } catch (e: Exception) {
                _errorMessage.value = "Failed to load books: ${e.message}"
                Log.e("HomeViewModel", "Error loading books: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun filterBooks(query: String) {
        if (query.isBlank()) {
            _trendingBooks.value = allBooksLoaded
            _fantasyBooks.value = allBooksLoaded.filter { it.kategori == "Fantasy" }
            _fictionBooks.value = allBooksLoaded.filter { it.kategori == "Fiction" }
            _selfHelpBooks.value = allBooksLoaded.filter { it.kategori == "Self-Help" }
        } else {
            val lowerCaseQuery = query.lowercase(Locale.ROOT) // <--- PERBAIKAN DI SINI
            val filteredList = allBooksLoaded.filter {
                it.judul.lowercase(Locale.ROOT).contains(lowerCaseQuery) || // <--- PERBAIKAN DI SINI
                        it.penulis.lowercase(Locale.ROOT).contains(lowerCaseQuery) || // <--- PERBAIKAN DI SINI
                        it.kategori.lowercase(Locale.ROOT).contains(lowerCaseQuery) // <--- PERBAIKAN DI SINI
            }

            _trendingBooks.value = filteredList
            _fantasyBooks.value = filteredList.filter { it.kategori == "Fantasy" }
            _fictionBooks.value = filteredList.filter { it.kategori == "Fiction" }
            _selfHelpBooks.value = filteredList.filter { it.kategori == "Self-Help" }
        }
    }
}