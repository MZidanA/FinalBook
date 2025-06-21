package com.insfinal.bookdforall.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.insfinal.bookdforall.R
import com.insfinal.bookdforall.adapters.BookAdapter
import com.insfinal.bookdforall.databinding.ActivityMainBinding
import com.insfinal.bookdforall.model.Book
import com.insfinal.bookdforall.network.RetrofitInstance // Import RetrofitInstance
import com.insfinal.bookdforall.data.BookData // <-- Import BookData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
        binding.rvLanjutkanBaca.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        // Gunakan data dari BookData untuk sementara
        val allBooks = BookData.getAllBooks() // <-- Gunakan BookData
        val continueReadingBooks = allBooks.take(3) // Contoh: 3 buku pertama

        binding.rvLanjutkanBaca.adapter = BookAdapter(continueReadingBooks) { clickedBook ->
            val intent = Intent(this@MainActivity, BookDetailActivity::class.java)
            intent.putExtra(BookDetailActivity.EXTRA_BOOK, clickedBook)
            startActivity(intent)
        }
    }

    private fun setupTrendingRecyclerView() {
        binding.rvTrending.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        // Gunakan data dari BookData untuk sementara
        val allBooks = BookData.getAllBooks() // <-- Gunakan BookData
        val trendingBooks = allBooks.drop(3).take(4) // Contoh: buku dari indeks 3 hingga 6

        binding.rvTrending.adapter = BookAdapter(trendingBooks) { clickedBook ->
            val intent = Intent(this@MainActivity, BookDetailActivity::class.java)
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
                    startActivity(Intent(this, CollectionActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }
}