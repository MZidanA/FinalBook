// AdminBookListFragment.kt
package com.insfinal.bookdforall.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.insfinal.bookdforall.databinding.FragmentAdminBookListBinding
import com.insfinal.bookdforall.model.Book
import com.insfinal.bookdforall.adapters.AdminBookAdapter
import com.insfinal.bookdforall.viewmodel.AdminBookViewModel
import kotlinx.coroutines.launch

class AdminBookListFragment : Fragment() {

    private var _binding: FragmentAdminBookListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AdminBookViewModel by viewModels()

    private lateinit var bookAdapter: AdminBookAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminBookListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()
        viewModel.loadBooks()

        binding.fabAddBook.setOnClickListener {
            startActivity(Intent(requireContext(), AddBookActivity::class.java))
        }
    }

    private fun setupRecyclerView() {
        bookAdapter = AdminBookAdapter(
            onEditClick = { book ->
                val intent = Intent(requireContext(), EditBookActivity::class.java)
                intent.putExtra(EditBookActivity.EXTRA_BOOK_ID, book.bookId)
                startActivity(intent)
            },
            onDeleteClick = { book ->
                showDeleteConfirmationDialog(book)
            }
        )
        binding.rvAdminBooks.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = bookAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.booksList.observe(viewLifecycleOwner) { books ->
            bookAdapter.updateData(books)
            binding.tvEmptyState.visibility = if (books.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.eventSuccess.collect { message ->
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                viewModel.loadBooks()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.eventError.collect { message ->
                Toast.makeText(requireContext(), "Error: $message", Toast.LENGTH_LONG).show()
                Log.e("AdminBookListFragment", "Error: $message")
            }
        }
    }

    private fun showDeleteConfirmationDialog(book: Book) {
        AlertDialog.Builder(requireContext())
            .setTitle("Hapus Buku")
            .setMessage("Apakah Anda yakin ingin menghapus buku '${book.judul}'?")
            .setPositiveButton("Hapus") { _, _ ->
                viewModel.deleteBook(book.bookId)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadBooks()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}