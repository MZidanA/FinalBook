package com.insfinal.bookdforall.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.insfinal.bookdforall.R // Pastikan ini benar untuk drawable
import com.insfinal.bookdforall.databinding.ItemBookHorizontalBinding // Asumsi layout itemnya
import com.insfinal.bookdforall.model.Book
import com.insfinal.bookdforall.utils.BookAssetPaths

class BookAdapter(
    private var books: List<Book>,
    private val onItemClick: (Book) -> Unit
) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    inner class BookViewHolder(private val binding: ItemBookHorizontalBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(book: Book) {
            val coverFileName = book.coverImageFileName
            if (coverFileName != null) {
                val assetPath = BookAssetPaths.ASSETS_ROOT + BookAssetPaths.ASSETS_IMG_PATH + coverFileName
                Glide.with(binding.ivBookCover.context)
                    .load(assetPath) // Memuat dari assets
                    .placeholder(R.drawable.placeholder_book_cover) // Pastikan placeholder ini ada
                    .error(R.drawable.error_book_cover) // Pastikan error drawable ini ada
                    .into(binding.ivBookCover)
            } else {
                // Fallback jika coverImageFileName null
                Glide.with(binding.ivBookCover.context)
                    .load(R.drawable.placeholder_book_cover)
                    .into(binding.ivBookCover)
            }

            binding.tvBookTitle.text = book.judul
            binding.tvBookAuthor.text = book.penulis

            itemView.setOnClickListener {
                onItemClick(book)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val binding =
            ItemBookHorizontalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(books[position])
    }

    override fun getItemCount(): Int = books.size

    fun updateData(newBooks: List<Book>) {
        books = newBooks
        notifyDataSetChanged()
    }
}