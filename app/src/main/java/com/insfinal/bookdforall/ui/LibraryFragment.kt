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
import com.insfinal.bookdforall.databinding.FragmentLibraryBinding
import com.insfinal.bookdforall.model.Book
import com.insfinal.bookdforall.network.RetrofitInstance
import com.insfinal.bookdforall.data.BookData // <-- Import BookData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LibraryFragment : Fragment() {

    private var _binding: FragmentLibraryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadLibraryBooks()

        binding.btnCariBuku.setOnClickListener {
            startActivity(Intent(requireContext(), MainActivity::class.java))
        }
    }

    private fun loadLibraryBooks() {
        val sharedPrefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userId = sharedPrefs.getInt("user_id", -1)

        if (userId != -1) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // API call untuk userBooks, ini akan tetap dari API jika UserBooks sudah di-set di BE
                    val userBooksResponse = RetrofitInstance.api.getUserBooksByUserId(userId)
                    // Menggunakan data dari BookData untuk semua buku yang tersedia
                    val allAvailableBooks = BookData.getAllBooks() // <-- Gunakan BookData

                    withContext(Dispatchers.Main) {
                        if (userBooksResponse.isSuccessful) { // Hanya cek respons userBooks
                            val userBooks = userBooksResponse.body() ?: emptyList()

                            val continueReadingBooks = mutableListOf<Book>()
                            val unreadBooks = mutableListOf<Book>()

                            userBooks.forEach { userBook ->
                                val book = allAvailableBooks.find { it.bookId == userBook.bookId } // Cari buku dari BookData
                                if (book != null) {
                                    when (userBook.statusBaca) {
                                        "sedang_membaca" -> continueReadingBooks.add(book)
                                        "belum_dibaca" -> unreadBooks.add(book)
                                        // Anda bisa menambahkan logika untuk status lain seperti "selesai_dibaca"
                                    }
                                }
                            }

                            if (continueReadingBooks.isEmpty() && unreadBooks.isEmpty()) {
                                binding.layoutEmptyCollection.visibility = View.VISIBLE
                                binding.tvLanjutMembacaTitle.visibility = View.GONE
                                binding.rvLanjutMembaca.visibility = View.GONE
                                binding.tvBelumDibacaTitle.visibility = View.GONE
                                binding.rvBelumDibaca.visibility = View.GONE
                            } else {
                                binding.layoutEmptyCollection.visibility = View.GONE

                                if (continueReadingBooks.isNotEmpty()) {
                                    binding.tvLanjutMembacaTitle.visibility = View.VISIBLE
                                    binding.rvLanjutMembaca.visibility = View.VISIBLE
                                    binding.rvLanjutMembaca.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                                    binding.rvLanjutMembaca.adapter = BookAdapter(continueReadingBooks) { book ->
                                        val intent = Intent(requireContext(), BookDetailActivity::class.java)
                                        intent.putExtra(BookDetailActivity.EXTRA_BOOK, book)
                                        startActivity(intent)
                                    }
                                } else {
                                    binding.tvLanjutMembacaTitle.visibility = View.GONE
                                    binding.rvLanjutMembaca.visibility = View.GONE
                                }

                                if (unreadBooks.isNotEmpty()) {
                                    binding.tvBelumDibacaTitle.visibility = View.VISIBLE
                                    binding.rvBelumDibaca.visibility = View.VISIBLE
                                    binding.rvBelumDibaca.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                                    binding.rvBelumDibaca.adapter = BookAdapter(unreadBooks) { book ->
                                        val intent = Intent(requireContext(), BookDetailActivity::class.java)
                                        intent.putExtra(BookDetailActivity.EXTRA_BOOK, book)
                                        startActivity(intent)
                                    }
                                } else {
                                    binding.tvBelumDibacaTitle.visibility = View.GONE
                                    binding.rvBelumDibaca.visibility = View.GONE
                                }
                            }
                        } else {
                            val errorBodyUserBooks = userBooksResponse.errorBody()?.string()
                            Toast.makeText(requireContext(), "Gagal memuat perpustakaan: ${errorBodyUserBooks ?: "Unknown error"}", Toast.LENGTH_LONG).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Error memuat perpustakaan: ${e.message}", Toast.LENGTH_LONG).show()
                        e.printStackTrace()
                    }
                }
            }
        } else {
            binding.layoutEmptyCollection.visibility = View.VISIBLE
            binding.tvLanjutMembacaTitle.visibility = View.GONE
            binding.rvLanjutMembaca.visibility = View.GONE
            binding.tvBelumDibacaTitle.visibility = View.GONE
            binding.rvBelumDibaca.visibility = View.GONE
            Toast.makeText(requireContext(), "Silakan login untuk melihat perpustakaan Anda.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onResume() {
        super.onResume()
        loadLibraryBooks()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}