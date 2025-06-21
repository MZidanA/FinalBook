package com.insfinal.bookdforall.data

import com.insfinal.bookdforall.model.Book // Pastikan mengimpor model Book

object BookData {
    // Definisi data buku (biodata)
    private val allBooks: List<Book> = listOf(
        Book(
            bookId = 1,
            judul = "The Alchemist",
            penulis = "Paulo Coelho",
            deskripsi = "A philosophical novel about a shepherd boy who journeys in search of treasure, learning valuable life lessons along the way.",
            kategori = "Fiction",
            harga = 12.99,
            format = "PDF", // Pastikan format sesuai dengan file di assets
            coverImageUrl = "https://i.gr-assets.com/images/S/compressed.photo.goodreads.com/books/1460314467l/865.jpg",
            publisherId = 101,
            contentProviderId = 201
        ),
        Book(
            bookId = 2,
            judul = "Sapiens: A Brief History of Humankind",
            penulis = "Yuval Noah Harari",
            deskripsi = "A sweeping narrative of the history of our species, from ancient times to the present day, exploring how biology and history have defined us.",
            kategori = "Non-Fiction",
            harga = 18.50,
            format = "PDF",
            coverImageUrl = "https://images-na.ssl-images-amazon.com/images/I/41K-j2xN+iL._SX331_BO1,204,203,200_.jpg",
            publisherId = 102,
            contentProviderId = 202
        ),
        Book(
            bookId = 3,
            judul = "The Lord of the Rings",
            penulis = "J.R.R. Tolkien",
            deskripsi = "An epic high-fantasy adventure set in Middle-earth, following the quest to destroy the One Ring and save the world from the Dark Lord Sauron.",
            kategori = "Fantasy",
            harga = 25.00,
            format = "PDF",
            coverImageUrl = "https://prodimage.images-bn.com/pimages/9780618053267_p0_v4_s600x595.jpg",
            publisherId = 103,
            contentProviderId = 203
        ),
        Book(
            bookId = 4,
            judul = "Atomic Habits",
            penulis = "James Clear",
            deskripsi = "An easy & proven way to build good habits & break bad ones, offering a practical framework for improving every day.",
            kategori = "Self-Help",
            harga = 15.75,
            format = "PDF",
            coverImageUrl = "https://m.media-amazon.com/images/I/51-nXYy5WYL.jpg",
            publisherId = 104,
            contentProviderId = 204
        ),
        Book(
            bookId = 5,
            judul = "The Midnight Library",
            penulis = "Matt Haig",
            deskripsi = "Between life and death, Nora Seed discovers a library containing endless versions of her life, and a chance to choose a different path.",
            kategori = "Fantasy",
            harga = 14.00,
            format = "PDF",
            coverImageUrl = "https://i.gr-assets.com/images/S/compressed.photo.goodreads.com/books/1586616222l/52578297._SY475_.jpg",
            publisherId = 105,
            contentProviderId = 205
        )
    )

    // Mapping BookId ke nama file di folder assets
    private val assetFileMap: Map<Int, String> = mapOf(
        1 to "the_alchemist.pdf",
        2 to "sapiens.pdf",
        3 to "the_lord_of_the_rings.pdf",
        4 to "atomic_habits.pdf",
        5 to "the_midnight_library.pdf"
        // Pastikan nama file ini sesuai dengan yang ada di folder assets/
    )

    fun getAllBooks(): List<Book> {
        return allBooks
    }

    fun getBookById(bookId: Int): Book? {
        return allBooks.find { it.bookId == bookId }
    }

    fun getAssetFileName(bookId: Int): String? {
        return assetFileMap[bookId]
    }
}