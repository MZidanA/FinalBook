package com.insfinal.bookdforall.ui.admin

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.booksforall.databinding.FragmentBookManageBinding

class BookManageFragment : Fragment() {
    private var _binding: FragmentBookManageBinding? = null
    private val binding get() = _binding!!
    private val viewModel: BookManageViewModel by viewModels()
    private lateinit var adapter: AdminBookAdapter
    var hasFadedIn = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookManageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loadBooks()
        adapter = AdminBookAdapter(
            onEditClick = { book ->
                val intent = Intent(requireContext(), EditBookActivity::class.java).apply {
                    putExtra("EXTRA_BOOK", book)
                }
                startActivity(intent)
            },
            onDeleteClick = { book -> viewModel.deleteBook(book.bookId) }
        )

        binding.myBooksRecyclerView.adapter = adapter


        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            val fadeDuration = 250L
            binding.loadingOverlay.animate()
                .alpha(if (isLoading) 1f else 0f)
                .setDuration(fadeDuration)
                .withStartAction {
                    if (!isLoading) {
                        binding.myBooksRecyclerView.apply {
                            alpha = 0f
                            animate().alpha(1f).setDuration(250L).start()
                        }
                    }
                }
                .withEndAction {
                    if (!isLoading) binding.loadingOverlay.visibility = View.GONE
                }
                .start()
            binding.myBooksRecyclerView.visibility = if (isLoading) View.GONE else View.VISIBLE
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                viewModel.clearErrorMessage()
            }
        }

        binding.searchView.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = binding.searchView.text.toString()
                viewModel.searchBooks(query)
                true
            } else false
        }

        binding.searchView.doAfterTextChanged {
            if (it.isNullOrBlank()) viewModel.resetSearch()
        }

        binding.fabAddBook.setOnClickListener {
            val intent = Intent(requireContext(), AddBookActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadBooks()
    }
}