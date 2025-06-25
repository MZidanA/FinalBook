package com.insfinal.bookdforall.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.insfinal.bookdforall.databinding.FragmentCollectionBinding
import com.insfinal.bookdforall.adapters.DownloadedBooksAdapter
import com.insfinal.bookdforall.model.DownloadedBook
import com.insfinal.bookdforall.utils.PrefManager
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import java.io.File
import android.util.Log

class CollectionFragment : Fragment() {

    private var _binding: FragmentCollectionBinding? = null
    private lateinit var adapter: DownloadedBooksAdapter
    private val binding get() = _binding!!
    private var allDownloadedBooks: List<DownloadedBook> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCollectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.myBooksRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        loadAndDisplayBooks()

        binding.searchView.addTextChangedListener { editable ->
            val query = editable.toString()
            val filtered = allDownloadedBooks.filter {
                // it.title dan it.author sekarang non-nullable String
                it.title.contains(query, ignoreCase = true) ||
                        it.author.contains(query, ignoreCase = true)
            }
            adapter.updateData(filtered)
            binding.emptyMyBooksView.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun loadAndDisplayBooks() {
        allDownloadedBooks = PrefManager.getDownloadedBooks(requireContext())
        Log.d("CollectionFragment", "Loaded ${allDownloadedBooks.size} downloaded books.")
        allDownloadedBooks.forEach {
            Log.d("CollectionFragment", "Downloaded Book: ${it.title} (${it.fileName})")
        }
        binding.emptyMyBooksView.visibility = if (allDownloadedBooks.isEmpty()) View.VISIBLE else View.GONE

        adapter = DownloadedBooksAdapter(allDownloadedBooks) { book ->
            val downloadsDir = File(requireContext().getExternalFilesDir(null), "books_downloaded")
            val pdfFile = File(downloadsDir, book.fileName)

            if (pdfFile.exists()) {
                val intent = Intent(requireContext(), PdfViewerActivity::class.java)
                intent.putExtra(PdfViewerActivity.EXTRA_BOOK_ID, book.bookId)
                intent.putExtra(PdfViewerActivity.EXTRA_BOOK_FILENAME, book.fileName)
                intent.putExtra(PdfViewerActivity.EXTRA_BOOK_TITLE, book.title)
                startActivity(intent)
            } else {
                Toast.makeText(requireContext(), "File buku tidak ditemukan: ${book.fileName}", Toast.LENGTH_SHORT).show()
                PrefManager.removeDownloadedBook(requireContext(), book.bookId)
                loadAndDisplayBooks()
            }
        }
        binding.myBooksRecyclerView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        loadAndDisplayBooks()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}