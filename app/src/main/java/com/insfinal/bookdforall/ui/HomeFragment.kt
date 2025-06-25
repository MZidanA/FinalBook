package com.insfinal.bookdforall.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener // Import ini
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.insfinal.bookdforall.databinding.FragmentHomeBinding
import com.insfinal.bookdforall.adapters.BookAdapter
import com.insfinal.bookdforall.model.Book
import com.insfinal.bookdforall.utils.BookAssetPaths
import com.insfinal.bookdforall.viewmodel.HomeViewModel
import java.util.Locale // Import Locale

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

        // Setup listener untuk search input
        binding.search.addTextChangedListener { editable ->
            val query = editable.toString()
            viewModel.filterBooks(query)
        }

        // Fungsi helper untuk menginisialisasi RecyclerView
        fun setupBookRecyclerView(recyclerView: androidx.recyclerview.widget.RecyclerView, books: List<Book>) {
            recyclerView.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            // Pastikan adapter diinisialisasi ulang atau datanya diupdate
            if (recyclerView.adapter == null) {
                recyclerView.adapter = BookAdapter(books) { clickedBook ->
                    val intent = Intent(requireContext(), BookDetailActivity::class.java)
                    intent.putExtra(BookDetailActivity.EXTRA_BOOK_ID, clickedBook.bookId)
                    intent.putExtra("book_object", clickedBook)
                    startActivity(intent)
                }
            } else {
                (recyclerView.adapter as? BookAdapter)?.updateData(books)
            }
            // Tampilkan/sembunyikan RecyclerView dan judul kategori jika daftar kosong
            val isListEmpty = books.isEmpty()
            val categoryTitleId = when (recyclerView.id) {
                binding.rvTrending.id -> binding.tvTrending
                binding.rvCategoryFantasy.id -> binding.tvCategoryFantasy
                binding.rvCategoryFiction.id -> binding.tvCategoryFiction
                binding.rvCategorySelfHelp.id -> binding.tvCategorySelfHelp
                else -> null
            }
            recyclerView.visibility = if (isListEmpty) View.GONE else View.VISIBLE
            categoryTitleId?.visibility = if (isListEmpty) View.GONE else View.VISIBLE
        }


        viewModel.trendingBooks.observe(viewLifecycleOwner) { books ->
            setupBookRecyclerView(binding.rvTrending, books)
            Log.d("HomeFragment", "Trending Books displayed: ${books.size}")
        }

        viewModel.fantasyBooks.observe(viewLifecycleOwner) { books ->
            setupBookRecyclerView(binding.rvCategoryFantasy, books)
            Log.d("HomeFragment", "Fantasy Books displayed: ${books.size}")
        }

        viewModel.fictionBooks.observe(viewLifecycleOwner) { books ->
            setupBookRecyclerView(binding.rvCategoryFiction, books)
            Log.d("HomeFragment", "Fiction Books displayed: ${books.size}")
        }

        viewModel.selfHelpBooks.observe(viewLifecycleOwner) { books ->
            setupBookRecyclerView(binding.rvCategorySelfHelp, books)
            Log.d("HomeFragment", "Self-Help Books displayed: ${books.size}")
        }


        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // Implementasikan loading state visual Anda di sini
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                Log.e("HomeFragment", it)
                Toast.makeText(requireContext(), "Error: $it", Toast.LENGTH_LONG).show()
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}