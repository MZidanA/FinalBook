    package com.insfinal.bookdforall.ui

    import android.os.Bundle
    import androidx.fragment.app.Fragment
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.Toast
    import com.example.booksforall.databinding.FragmentCollectionBinding
    import com.insfinal.bookdforall.adapters.DownloadedBooksAdapter
    import com.insfinal.bookdforall.model.DownloadedBook
    import java.io.File
    import androidx.core.widget.addTextChangedListener

    class CollectionFragment : Fragment() {

        private var _binding: FragmentCollectionBinding? = null
        private lateinit var adapter: DownloadedBooksAdapter
        private val binding get() = _binding!!

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {

            _binding = FragmentCollectionBinding.inflate(inflater, container, false)
            return binding.root
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            val books = loadDownloadedBooks()

            binding.emptyMyBooksView.visibility = if (books.isEmpty()) View.VISIBLE else View.GONE

            adapter = DownloadedBooksAdapter(books) { book ->
                Toast.makeText(requireContext(), "Buka file: ${book.fileName}", Toast.LENGTH_SHORT)
                    .show()
            }
            binding.myBooksRecyclerView.adapter = adapter

            binding.searchView.addTextChangedListener { editable ->
                val query = editable.toString()
                val filtered = books.filter {
                    it.fileName.contains(query, ignoreCase = true)
                }
                adapter.updateData(filtered)
                binding.emptyMyBooksView.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
            }
        }


        private fun loadDownloadedBooks(): List<DownloadedBook> {
            val downloadsDir = File(requireContext().getExternalFilesDir(null), "books_downloaded")
            return downloadsDir.listFiles()
                ?.filter { it.extension == "pdf" || it.extension == "epub" }
                ?.map { DownloadedBook(it.name, it.absolutePath) }
                ?: emptyList()
        }

        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }
    }