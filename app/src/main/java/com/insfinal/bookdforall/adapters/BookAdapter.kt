package com.insfinal.bookdforall.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.insfinal.bookdforall.databinding.ItemBookHorizontalBinding
import com.insfinal.bookdforall.model.Book

// Tambahkan parameter clickListener ke konstruktor
class BookAdapter(
    private val books: List<Book>,
    private val onItemClick: (Book) -> Unit // Lambda untuk menangani klik item
) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    inner class BookViewHolder(private val binding: ItemBookHorizontalBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(book: Book) {
            binding.tvBookTitle.text = book.judul
            binding.tvBookAuthor.text = book.penulis

            Glide.with(binding.ivBookCover.context)
                .load(book.coverImageUrl)
                .placeholder(com.insfinal.bookdforall.R.drawable.placeholder_book_cover)
                .error(com.insfinal.bookdforall.R.drawable.placeholder_book_cover)
                .into(binding.ivBookCover)

            // Set OnClickListener untuk seluruh item
            binding.root.setOnClickListener {
                onItemClick(book) // Panggil lambda ketika item diklik
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val binding = ItemBookHorizontalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(books[position])
    }

    override fun getItemCount(): Int = books.size
}