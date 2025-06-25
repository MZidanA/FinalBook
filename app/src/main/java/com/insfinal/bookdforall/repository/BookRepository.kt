package com.insfinal.bookdforall.repository

import android.content.Context // <--- TAMBAHKAN INI
import android.net.Uri // <--- TAMBAHKAN INI
import com.insfinal.bookdforall.model.Book
import com.insfinal.bookdforall.model.CreateBookRequest
import com.insfinal.bookdforall.network.RetrofitInstance
import com.insfinal.bookdforall.utils.BookAssetPaths
import com.insfinal.bookdforall.utils.FileUtils // <--- TAMBAHKAN INI
import okhttp3.MediaType.Companion.toMediaTypeOrNull // <--- TAMBAHKAN INI
import okhttp3.MultipartBody // <--- TAMBAHKAN INI
import okhttp3.RequestBody.Companion.asRequestBody // <--- TAMBAHKAN INI (untuk file.asRequestBody)
import okhttp3.RequestBody.Companion.toRequestBody // <--- TAMBAHKAN INI (untuk string.toRequestBody)
import retrofit2.Response
import java.io.File // Ensure correct import for File

class BookRepository {
    private val api = RetrofitInstance.api

    suspend fun getAll(): Response<List<Book>> = api.getBooks()

    suspend fun getBookById(id: Int): Response<Book> = api.getBookById(id)

    suspend fun create(req: CreateBookRequest): Response<Unit> = api.createBook(req)

    suspend fun update(id: Int, req: CreateBookRequest): Response<Unit> = api.updateBook(id, req)

    // PERUBAHAN: Tambahkan return type Response<Unit>
    suspend fun uploadBook(
        context: Context,
        title: String,
        author: String,
        description: String,
        coverUri: Uri?,
        pdfUri: Uri?
    ): Response<Unit> { // <--- TAMBAHKAN RETURN TYPE
        val titlePart = title.toRequestBody("text/plain".toMediaTypeOrNull())
        val authorPart = author.toRequestBody("text/plain".toMediaTypeOrNull())
        val descPart = description.toRequestBody("text/plain".toMediaTypeOrNull())

        val coverPart = coverUri?.let { uri ->
            val file = FileUtils.fromUri(context, uri)
            file.asRequestBody("image/*".toMediaTypeOrNull())
                .let { body -> MultipartBody.Part.createFormData("cover", file.name, body) }
        }

        val pdfPart = pdfUri?.let { uri ->
            val file = FileUtils.fromUri(context, uri)
            file.asRequestBody("application/pdf".toMediaTypeOrNull())
                .let { body -> MultipartBody.Part.createFormData("pdf", file.name, body) }
        }

        return api.uploadBook(titlePart, authorPart, descPart, coverPart, pdfPart) // Tambahkan 'return'
    }

    suspend fun delete(id: Int): Response<Unit> = api.deleteBook(id)

    suspend fun getContinueReadingBooks(): Response<List<Book>> = api.getContinueReading()

    suspend fun getTrendingBooks(): Response<List<Book>> = api.getTrendingBooks()

    suspend fun getLocalDummyBooks(): List<Book> {
        // ... (kode dummy books tetap sama) ...
        val dummyBooks = listOf(
            Book(
                bookId = 101, // PENTING: bookId ini harus sesuai dengan BookAssetPaths
                judul = "Atomic Habits",
                penulis = "James Clear",
                deskripsi = "Sebuah metode yang telah terbukti untuk membangun kebiasaan baik dan menghilangkan kebiasaan buruk. Buku ini akan mengubah cara Anda berpikir tentang kemajuan dan keberhasilan.",
                kategori = "Self-Help",
                harga = 0.0,
                format = "PDF",
                rating = 4.7f,
                ratingCount = 1500,
                releaseDate = "16 October 2018",
                totalPages = 320,
                coverImageUrl = "placeholder_cover_url_if_any",
                publisherId = 1,
                contentProviderId = 1,
                pdfFileName = "atomic_habits.pdf" // Penting untuk dummy data lokal
            ),
            Book(
                bookId = 102, // PENTING: bookId ini harus sesuai dengan BookAssetPaths
                judul = "C++ Tutorial",
                penulis = "Various",
                deskripsi = "Panduan lengkap untuk belajar pemrograman C++ dari dasar hingga tingkat lanjut. Meliputi konsep-konsep inti, struktur data, dan algoritma.",
                kategori = "Programming",
                harga = 0.0,
                format = "PDF",
                rating = 4.5f,
                ratingCount = 800,
                releaseDate = "Unknown",
                totalPages = 250,
                coverImageUrl = "placeholder_cover_url_if_any",
                publisherId = 2,
                contentProviderId = 2,
                pdfFileName = "C++ tutorial.pdf"
            ),
            Book(
                bookId = 103,
                judul = "Sapiens: A Brief History of Humankind",
                penulis = "Yuval Noah Harari",
                deskripsi = "Sebuah eksplorasi luas tentang sejarah manusia, dari Homo sapiens hingga revolusi kognitif, pertanian, dan ilmiah. Menawarkan perspektif yang menantang dan mendalam.",
                kategori = "History",
                harga = 0.0,
                format = "PDF",
                rating = 4.8f,
                ratingCount = 2000,
                releaseDate = "2011",
                totalPages = 500,
                coverImageUrl = "placeholder_cover_url_if_any",
                publisherId = 3,
                contentProviderId = 3,
                pdfFileName = "sapiens.pdf"
            ),
            Book(
                bookId = 104,
                judul = "The Alchemist",
                penulis = "Paulo Coelho",
                deskripsi = "Kisah inspiratif tentang seorang anak gembala yang melakukan perjalanan untuk menemukan takdirnya. Sebuah alegori yang kuat tentang impian, nasib, dan takdir.",
                kategori = "Fiction",
                harga = 0.0,
                format = "PDF",
                rating = 4.6f,
                ratingCount = 1200,
                releaseDate = "1988",
                totalPages = 200,
                coverImageUrl = "placeholder_cover_url_if_any",
                publisherId = 4,
                contentProviderId = 4,
                pdfFileName = "the_alchemist.pdf"
            ),
            Book(
                bookId = 105,
                judul = "The Lord of the Rings",
                penulis = "J.R.R. Tolkien",
                deskripsi = "Karya fantasi epik yang mengikuti petualangan Frodo Baggins dan persekutuan untuk menghancurkan Cincin Satu. Sebuah cerita klasik tentang kebaikan melawan kejahatan.",
                kategori = "Fantasy",
                harga = 0.0,
                format = "PDF",
                rating = 4.9f,
                ratingCount = 2500,
                releaseDate = "1954",
                totalPages = 1200,
                coverImageUrl = "placeholder_cover_url_if_any",
                publisherId = 5,
                contentProviderId = 5,
                pdfFileName = "the_lord_of_the_rings.pdf"
            ),
            Book(
                bookId = 106,
                judul = "The Midnight Library",
                penulis = "Matt Haig",
                deskripsi = "Antara hidup dan mati, ada sebuah perpustakaan. Ketika Nora Seed menemukan dirinya di sana, dia memiliki kesempatan untuk mencoba kehidupan lain yang mungkin pernah dia jalani. Sebuah kisah menyentuh tentang pilihan dan penyesalan.",
                kategori = "Fiction",
                harga = 0.0,
                format = "PDF",
                rating = 4.7f,
                ratingCount = 1000,
                releaseDate = "2020",
                totalPages = 300,
                coverImageUrl = "placeholder_cover_url_if_any",
                publisherId = 6,
                contentProviderId = 6,
                pdfFileName = "the_midnight_library.pdf"
            )
        )

        // Map dummyBooks untuk menambahkan pdfFileName dan coverImageFileName dari BookAssetPaths
        return dummyBooks.map { book ->
            book.copy(
                pdfFileName = BookAssetPaths.getPdfFileName(book.bookId),
                coverImageFileName = BookAssetPaths.getCoverImageFileName(book.bookId)
            )
        }
    }
}