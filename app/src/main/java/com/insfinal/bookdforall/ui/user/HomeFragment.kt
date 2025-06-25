package com.insfinal.bookdforall.ui.user

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.booksforall.databinding.FragmentHomeBinding
import com.insfinal.bookdforall.adapters.BookAdapter
import com.insfinal.bookdforall.network.RetrofitInstance
import com.insfinal.bookdforall.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            try {
                val books = RetrofitInstance.api.getBooks()
                Log.d("BOOKS", books.toString())
            } catch (e: Exception) {
                Log.e("ERROR", e.message ?: "Unknown error")
            }
        }

        viewModel.loadBooks()

        viewModel.continueReadingBooks.observe(viewLifecycleOwner) { books ->
            binding.rvLanjutkanBaca.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            binding.rvLanjutkanBaca.adapter = BookAdapter(books) { clickedBook ->
                val intent = Intent(requireContext(), BookDetailActivity::class.java)
                intent.putExtra(BookDetailActivity.EXTRA_BOOK_ID, clickedBook.bookId)
                startActivity(intent)
            }
        }

        viewModel.trendingBooks.observe(viewLifecycleOwner) { books ->
            binding.rvTrending.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            binding.rvTrending.adapter = BookAdapter(books) { clickedBook ->
                val intent = Intent(requireContext(), BookDetailActivity::class.java)
                intent.putExtra(BookDetailActivity.EXTRA_BOOK_ID, clickedBook.bookId)
                startActivity(intent)
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // Tampilkan atau sembunyikan shimmer / loader
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                Log.e("HomeFragment", it)
                // atau tampilkan toast/snackbar
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}