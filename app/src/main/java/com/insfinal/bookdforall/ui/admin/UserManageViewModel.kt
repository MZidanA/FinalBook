package com.insfinal.bookdforall.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.insfinal.bookdforall.model.User
import com.insfinal.bookdforall.repository.UserRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class UserManageViewModel : ViewModel() {

    private val repository = UserRepository()

    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> = _users

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _eventSuccess = MutableSharedFlow<String>()
    val eventSuccess = _eventSuccess.asSharedFlow()

    private val _eventError = MutableSharedFlow<String>()
    val eventError = _eventError.asSharedFlow()

    fun loadUsers() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getAllUsers()
                if (response.isSuccessful) {
                    val data = response.body()
                    _users.value = if (!data.isNullOrEmpty()) data else emptyList()
                } else {
                    _eventError.emit("Gagal memuat data user: ${response.code()}")
                }
            } catch (e: Exception) {
                _eventError.emit("Error: ${e.localizedMessage}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteUser(id: Int) {
        viewModelScope.launch {
            try {
                val response = repository.deleteUser(id)
                if (response.isSuccessful) {
                    loadUsers()
                    _eventSuccess.emit("User berhasil dihapus")
                } else {
                    _eventError.emit("Gagal menghapus user: ${response.code()}")
                }
            } catch (e: Exception) {
                _eventError.emit("Error: ${e.localizedMessage}")
            }
        }
    }

    private val _searchQuery = MutableLiveData("")
    val searchQuery: LiveData<String> = _searchQuery

    val filteredUsers: LiveData<List<User>> = MediatorLiveData<List<User>>().apply {
        addSource(users) { value = filter(users.value, searchQuery.value) }
        addSource(_searchQuery) { value = filter(users.value, searchQuery.value) }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    private fun filter(users: List<User>?, query: String?): List<User> {
        if (users == null || query.isNullOrBlank()) return users ?: emptyList()
        val q = query.lowercase()
        return users.filter {
            it.nama.lowercase().contains(q) || it.email.lowercase().contains(q)
        }
    }
}