package com.insfinal.bookdforall.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.insfinal.bookdforall.adapters.BookAdapter
import com.insfinal.bookdforall.databinding.FragmentBookmarksBinding
import com.insfinal.bookdforall.model.Book
import com.insfinal.bookdforall.network.RetrofitInstance
import com.insfinal.bookdforall.data.BookData // <-- Import BookData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BookmarksFragment : Fragment() {

    private var _binding: FragmentBookmarksBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookmarksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadBookmarkedBooks()

        binding.btnCariBukuBookmark.setOnClickListener {
            startActivity(Intent(requireContext(), MainActivity::class.java))
        }
    }

    private fun loadBookmarkedBooks() {
        val sharedPrefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userId = sharedPrefs.getInt("user_id", -1)

        if (userId != -1) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val bookmarksResponse = RetrofitInstance.api.getBookmarks(userId) // GET /bookmarks?userId=
                    val allAvailableBooks = BookData.getAllBooks() // <-- Gunakan BookData

                    withContext(Dispatchers.Main) {
                        if (bookmarksResponse.isSuccessful) {
                            val bookmarks = bookmarksResponse.body() ?: emptyList()

                            val bookmarkedBooks = mutableListOf<Book>()
                            bookmarks.forEach { bookmark ->
                                val book = allAvailableBooks.find { it.bookId == bookmark.bookId } // Cari buku dari BookData
                                if (book != null) {
                                    bookmarkedBooks.add(book)
                                }
                            }

                            if (bookmarkedBooks.isEmpty()) {
                                binding.layoutEmptyBookmarks.visibility = View.VISIBLE
                                binding.rvBookmarks.visibility = View.GONE
                            } else {
                                binding.layoutEmptyBookmarks.visibility = View.GONE
                                binding.rvBookmarks.visibility = View.VISIBLE
                                binding.rvBookmarks.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                                binding.rvBookmarks.adapter = BookAdapter(bookmarkedBooks) { book ->
                                    val intent = Intent(requireContext(), BookDetailActivity::class.java)
                                    intent.putExtra(BookDetailActivity.EXTRA_BOOK, book)
                                    startActivity(intent)
                                }
                            }
                        } else {
                            val errorBodyBookmarks = bookmarksResponse.errorBody()?.string()
                            Toast.makeText(requireContext(), "Gagal memuat bookmark: ${errorBodyBookmarks ?: "Unknown error"}", Toast.LENGTH_LONG).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Error memuat bookmark: ${e.message}", Toast.LENGTH_LONG).show()
                        e.printStackTrace()
                    }
                }
            }
        } else {
            binding.layoutEmptyBookmarks.visibility = View.VISIBLE
            binding.rvBookmarks.visibility = View.GONE
            Toast.makeText(requireContext(), "Silakan login untuk melihat bookmark Anda.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onResume() {
        super.onResume()
        loadBookmarkedBooks()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}