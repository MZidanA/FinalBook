package com.insfinal.bookdforall.ui // Sesuaikan dengan package Anda

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.layout.layout
import androidx.compose.ui.semantics.dismiss
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.insfinal.bookdforall.R

// Data class untuk User (tetap sama)
data class User(
    val uid: String,
    val name: String,
    val email: String,

)

class ManageUserActivity : AppCompatActivity() {

    private lateinit var recyclerViewUsers: RecyclerView
    private lateinit var userAdapter: UserAdapter
    private val userList = mutableListOf<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_user)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbarManageUser)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Kelola Pengguna"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        recyclerViewUsers = findViewById(R.id.recyclerViewUsers)
        setupRecyclerView()
        loadDummyUsers() // Atau load dari sumber data Anda
    }

    private fun setupRecyclerView() {
        userAdapter = UserAdapter(
            userList,
            onDeleteClick = { user, position ->
                showDeleteConfirmationDialog(user, position)
            },
            onItemClick = { user ->
                // Aksi jika ingin melihat detail user saat item (bukan tombol delete) di-klik
                Snackbar.make(recyclerViewUsers, "Detail untuk: ${user.name}", Snackbar.LENGTH_SHORT).show()
                // TODO: Buka halaman detail user jika diperlukan
            }
        )
        recyclerViewUsers.layoutManager = LinearLayoutManager(this)
        recyclerViewUsers.adapter = userAdapter
    }

    private fun showDeleteConfirmationDialog(user: User, position: Int) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Pengguna")
            .setMessage("Apakah Anda yakin ingin menghapus pengguna '${user.name}'?")
            .setIcon(R.drawable.ic_warning) // Pastikan Anda punya drawable ic_warning
            .setPositiveButton("Hapus") { dialog, _ ->
                deleteUser(user, position)
                dialog.dismiss()
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun deleteUser(user: User, position: Int) {
        // TODO: Implementasikan logika penghapusan pengguna dari backend/database Anda
        // Misalnya, jika menggunakan Firebase:
        // FirebaseFirestore.getInstance().collection("users").document(user.uid).delete()
        //     .addOnSuccessListener {
        //         userList.removeAt(position)
        //         userAdapter.notifyItemRemoved(position)
        //         userAdapter.notifyItemRangeChanged(position, userList.size) // Untuk update posisi
        //         Snackbar.make(recyclerViewUsers, "Pengguna '${user.name}' berhasil dihapus.", Snackbar.LENGTH_LONG).show()
        //     }
        //     .addOnFailureListener { e ->
        //         Snackbar.make(recyclerViewUsers, "Gagal menghapus pengguna: ${e.message}", Snackbar.LENGTH_LONG).show()
        //     }

        // Untuk contoh dummy, kita hapus dari list lokal saja:
        userList.removeAt(position)
        userAdapter.notifyItemRemoved(position)
        // Penting: Jika Anda tidak menghapus dari ujung list, Anda perlu memberitahu adapter tentang perubahan range
        userAdapter.notifyItemRangeChanged(position, userList.size - position)
        Snackbar.make(recyclerViewUsers, "Pengguna '${user.name}' telah dihapus (dummy).", Snackbar.LENGTH_LONG).show()
    }

    private fun loadDummyUsers() {
        userList.clear()
        userList.add(User("uid123", "Alice Wonderland", "alice@example.com", "Pengguna"))
        userList.add(User("uid456", "Bob The Builder", "bob@example.com", "Admin"))
        userList.add(User("uid789", "Charlie Brown", "charlie@example.com", "Pengguna"))
        userList.add(User("uid000", "Admin Utama", "admin@bookdforall.com", "Super Admin"))
        userAdapter.notifyDataSetChanged()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}

// UserAdapter dimodifikasi untuk menangani klik pada tombol hapus
class UserAdapter(
    private val userList: MutableList<User>,
    private val onDeleteClick: (User, Int) -> Unit, // Callback untuk tombol delete
    private val onItemClick: (User) -> Unit        // Callback untuk klik item
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R