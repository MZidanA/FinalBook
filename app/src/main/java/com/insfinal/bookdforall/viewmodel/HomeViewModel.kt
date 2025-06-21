package com.insfinal.bookdforall.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.insfinal.bookdforall.model.Book
import com.insfinal.bookdforall.repository.BookRepository
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val repository = BookRepository()

    private val _continueReadingBooks = MutableLiveData<List<Book>>()
    val continueReadingBooks: LiveData<List<Book>> = _continueReadingBooks

    private val _trendingBooks = MutableLiveData<List<Book>>()
    val trendingBooks: LiveData<List<Book>> = _trendingBooks

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun loadBooks() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val continueResponse = repository.getContinueReadingBooks()
                val trendingResponse = repository.getTrendingBooks()

                if (continueResponse.isSuccessful) {
                    _continueReadingBooks.value = continueResponse.body()
                } else {
                    _errorMessage.value = "Gagal memuat Continue Reading (${continueResponse.code()})"
                }

                if (trendingResponse.isSuccessful) {
                    _trendingBooks.value = trendingResponse.body()
                } else {
                    _errorMessage.value = "Gagal memuat Trending (${trendingResponse.code()})"
                }

            } catch (e: Exception) {
                _errorMessage.value = "Terjadi error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}