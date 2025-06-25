package com.insfinal.bookdforall.ui.admin

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.booksforall.databinding.FragmentUserManageBinding
import androidx.core.widget.addTextChangedListener

class UserManageFragment : Fragment() {
    private var _binding: FragmentUserManageBinding? = null
    private val binding get() = _binding!!
    private val viewModel: UserManageViewModel by viewModels()
    private lateinit var adapter: AdminUserAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserManageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = AdminUserAdapter(
            onDeleteClick = { user ->
                AlertDialog.Builder(requireContext())
                    .setTitle("Konfirmasi")
                    .setMessage("Yakin ingin menghapus ${user.nama}?")
                    .setPositiveButton("Hapus") { _, _ ->
                        viewModel.deleteUser(user.userId)
                    }
                    .setNegativeButton("Batal", null)
                    .show()
            }

        )
        binding.rvAdminUsers.adapter = adapter

        viewModel.loadUsers()

        binding.searchView.addTextChangedListener { editable ->
            viewModel.setSearchQuery(editable.toString())
        }

        viewModel.filteredUsers.observe(viewLifecycleOwner) { userList ->
            adapter.submitList(userList)
            binding.emptyUserView.visibility = if (userList.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}