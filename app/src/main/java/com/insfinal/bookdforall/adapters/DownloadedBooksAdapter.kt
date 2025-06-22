package com.insfinal.bookdforall.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.booksforall.databinding.CollectionItemBookBinding
import com.insfinal.bookdforall.model.DownloadedBook
import android.view.LayoutInflater

class DownloadedBooksAdapter(
    private var books: List<DownloadedBook>,
    private val onClick: (DownloadedBook) -> Unit
) : RecyclerView.Adapter<DownloadedBooksAdapter.BookViewHolder>() {

    inner class BookViewHolder(val binding: CollectionItemBookBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(book: DownloadedBook) {
            binding.bookTitle.text = book.fileName
            binding.root.setOnClickListener { onClick(book) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CollectionItemBookBinding.inflate(inflater, parent, false)
        return BookViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(books[position])
    }

    fun updateData(newBooks: List<DownloadedBook>) {
        this.books = newBooks
        notifyDataSetChanged()
    }

    override fun getItemCount() = books.size
}