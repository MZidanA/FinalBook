package com.insfinal.bookdforall.ui.admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.booksforall.R
import com.example.booksforall.databinding.ItemAdminBookBinding
import com.insfinal.bookdforall.model.Book

class AdminBookAdapter(
    private val onEditClick: (Book) -> Unit,
    private val onDeleteClick: (Book) -> Unit
) : ListAdapter<Book, AdminBookAdapter.BookViewHolder>(DIFF_CALLBACK) {

    inner class BookViewHolder(val binding: ItemAdminBookBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(book: Book) {
            binding.bookTitle.text = book.judul

            binding.bookAuthor.text = book.penulis

            val ratingText = book.rating?.let { String.format("%.1f", it) } ?: "0.0"
            binding.bookRating.text = ratingText

            Glide.with(binding.root)
                .load(book.coverImageUrl)
                .placeholder(R.drawable.placeholder_cover)
                .into(binding.bookCover)

            val editCallback = View.OnClickListener { onEditClick(book) }
            binding.adminEditBookButton.setOnClickListener(editCallback)
            binding.root.setOnClickListener(editCallback)

            binding.adminDeleteBookButton.setOnClickListener {
                onDeleteClick(book)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val binding = ItemAdminBookBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Book>() {
            override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean =
                oldItem.bookId == newItem.bookId

            override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean =
                oldItem == newItem
        }
    }
}