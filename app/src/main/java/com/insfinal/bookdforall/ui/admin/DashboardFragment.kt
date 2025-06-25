package com.insfinal.bookdforall.ui.admin

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.booksforall.databinding.FragmentDashboardBinding
import com.insfinal.bookdforall.ui.user.LoginActivity

class DashboardFragment : Fragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val userViewModel: UserManageViewModel by viewModels()
    private val bookViewModel: BookManageViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        userViewModel.loadUsers()
        bookViewModel.loadBooks()

        userViewModel.users.observe(viewLifecycleOwner) { users ->
            binding.totalUsers.text = "Total Pengguna: ${users.size}"
        }

        bookViewModel.books.observe(viewLifecycleOwner) { books ->
            binding.totalBooks.text = "Total Buku: ${books.size}"
        }

        binding.logoutButton.setOnClickListener {
            android.app.AlertDialog.Builder(requireContext())
                .setTitle("Konfirmasi Keluar")
                .setMessage("Apakah Anda yakin ingin logout?")
                .setPositiveButton("Ya") { _, _ ->
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    requireActivity().finish()
                }
                .setNegativeButton("Batal", null)
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}