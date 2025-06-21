package com.insfinal.bookdforall.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.insfinal.booksforall.R
import com.insfinal.booksforall.databinding.ActivityMainBinding
import com.insfinal.bookdforall.adapters.BookAdapter
import com.insfinal.bookdforall.model.Book
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        updateTime()
        setupContinueReadingRecyclerView()
        setupTrendingRecyclerView()
        setupBottomNavigation()
    }

    private fun updateTime() {
        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        binding.tvTime.text = currentTime
    }

    private fun setupContinueReadingRecyclerView() {
        val continueReadingBooks = listOf(
            Book(
                bookId = 1,
                judul = "The Alchemist",
                penulis = "Paulo Coelho",
                deskripsi = "A philosophical novel about a shepherd boy who journeys in search of treasure.",
                kategori = "Fiction",
                harga = 12.99,
                format = "eBook",
                coverImageUrl = "https://i.gr-assets.com/images/S/compressed.photo.goodreads.com/books/1460314467l/865.jpg",
                publisherId = 101,
                contentProviderId = 201
            ),
            Book(
                bookId = 2,
                judul = "Sapiens: A Brief History of Humankind",
                penulis = "Yuval Noah Harari",
                deskripsi = "A brief history of our species.",
                kategori = "Non-Fiction",
                harga = 18.50,
                format = "eBook",
                coverImageUrl = "https://images-na.ssl-images-amazon.com/images/I/41K-j2xN+iL._SX331_BO1,204,203,200_.jpg",
                publisherId = 102,
                contentProviderId = 202
            ),
            Book(
                bookId = 3,
                judul = "The Lord of the Rings",
                penulis = "J.R.R. Tolkien",
                deskripsi = "A high-fantasy adventure story.",
                kategori = "Fantasy",
                harga = 25.00,
                format = "eBook",
                coverImageUrl = "https://prodimage.images-bn.com/pimages/9780618053267_p0_v4_s600x595.jpg",
                publisherId = 103,
                contentProviderId = 203
            )
        )

        binding.rvLanjutkanBaca.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        // Lewatkan lambda onItemClick ke adapter
        binding.rvLanjutkanBaca.adapter = BookAdapter(continueReadingBooks) { clickedBook ->
            val intent = Intent(this, BookDetailActivity::class.java)
            intent.putExtra(BookDetailActivity.EXTRA_BOOK, clickedBook)
            startActivity(intent)
        }
    }

    private fun setupTrendingRecyclerView() {
        val trendingBooks = listOf(
            Book(
                bookId = 4,
                judul = "Atomic Habits",
                penulis = "James Clear",
                deskripsi = "An easy & proven way to build good habits & break bad ones.",
                kategori = "Self-Help",
                harga = 15.75,
                format = "eBook",
                coverImageUrl = "https://m.media-amazon.com/images/I/51-nXYy5WYL.jpg",
                publisherId = 104,
                contentProviderId = 204
            ),
            Book(
                bookId = 5,
                judul = "The Midnight Library",
                penulis = "Matt Haig",
                deskripsi = "A woman gets a chance to revisit different versions of her life.",
                kategori = "Fantasy",
                harga = 14.00,
                format = "eBook",
                coverImageUrl = "https://i.gr-assets.com/images/S/compressed.photo.goodreads.com/books/1586616222l/52578297._SY475_.jpg",
                publisherId = 105,
                contentProviderId = 205
            ),
            Book(
                bookId = 6,
                judul = "Project Hail Mary",
                penulis = "Andy Weir",
                deskripsi = "An astronaut must save humanity.",
                kategori = "Science Fiction",
                harga = 17.25,
                format = "eBook",
                coverImageUrl = "https://images-na.ssl-images-amazon.com/images/I/41mS7bF0hDL._SY291_BO1,204,203,200_QL40_FM.jpg",
                publisherId = 106,
                contentProviderId = 206
            ),
            Book(
                bookId = 7,
                judul = "Dune",
                penulis = "Frank Herbert",
                deskripsi = "A saga of political intrigue and environmentalism on a desert planet.",
                kategori = "Science Fiction",
                harga = 19.50,
                format = "eBook",
                coverImageUrl = "https://images-na.ssl-images-amazon.com/images/I/41-lUvEw5AL._SX322_BO1,204,203,200_.jpg",
                publisherId = 107,
                contentProviderId = 207
            )
        )

        binding.rvTrending.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        // Lewatkan lambda onItemClick ke adapter
        binding.rvTrending.adapter = BookAdapter(trendingBooks) { clickedBook ->
            val intent = Intent(this, BookDetailActivity::class.java)
            intent.putExtra(BookDetailActivity.EXTRA_BOOK, clickedBook)
            startActivity(intent)
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    Toast.makeText(this, "Home Clicked", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_Collection -> {
                    Toast.makeText(this, "Favorites Clicked", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_profile -> {
                    Toast.makeText(this, "Profile Clicked", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
    }
}