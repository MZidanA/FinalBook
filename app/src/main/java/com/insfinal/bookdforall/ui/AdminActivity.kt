package com.insfinal.bookdforall.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.insfinal.bookdforall.R
import com.insfinal.bookdforall.databinding.ActivityAdminBinding
import com.insfinal.bookdforall.utils.SessionManager

class AdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up ActionBar for AdminActivity (default title)
        setupToolbar("Admin Dashboard", false) // false means no back button initially

        // Initial state: show dashboard, hide fragment container
        showDashboardView()

        // Listener for "Manage Users"
        binding.btnKelolaPengguna.setOnClickListener {
            // Load UserManageFragment into the container
            loadFragment(UserManageFragment(), "UserManageFragment")
            setupToolbar("Kelola Pengguna", true) // Show back button
        }

        // Listener for "Manage Books"
        binding.btnKelolaBuku.setOnClickListener {
            loadFragment(AdminBookListFragment(), "AdminBookListFragment") // Use AdminBookListFragment
            setupToolbar("Kelola Buku", true) // Show back button
        }

        // Listener for Logout
        binding.btnAdminLogout.setOnClickListener {
            SessionManager.logoutUser()
            Toast.makeText(this, "Admin Logout Berhasil!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
            finish()
        }

        // Handle initial fragment if coming from state restore or deep link
        if (savedInstanceState == null) {
            // Optionally load a default fragment here if you don't want to show dashboard first
            // or perform initial data loading for dashboard summary
        }
    }

    private fun setupToolbar(title: String, showBackButton: Boolean) {
        supportActionBar?.title = title
        supportActionBar?.setDisplayHomeAsUpEnabled(showBackButton)
    }

    private fun showDashboardView() {
        binding.adminDashboardScrollView.visibility = View.VISIBLE
        binding.adminFragmentContainer.visibility = View.GONE
    }

    private fun showFragmentContainer() {
        binding.adminDashboardScrollView.visibility = View.GONE
        binding.adminFragmentContainer.visibility = View.VISIBLE
    }

    // Helper function to load fragments into the container
    private fun loadFragment(fragment: Fragment, tag: String) {
        showFragmentContainer() // Ensure container is visible

        supportFragmentManager.beginTransaction()
            .replace(R.id.admin_fragment_container, fragment, tag)
            .addToBackStack(tag) // Add to back stack with a tag
            .commit()
    }

    // Handle back button press
    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 1) { // If there's more than just the initial entry
            supportFragmentManager.popBackStack()
            // Check current fragment after popping
            val currentFragment = supportFragmentManager.findFragmentById(R.id.admin_fragment_container)
            if (currentFragment is UserManageFragment) {
                setupToolbar("Kelola Pengguna", true)
            } else if (currentFragment is AdminBookListFragment) {
                setupToolbar("Kelola Buku", true)
            } else {
                setupToolbar("Admin Dashboard", false) // Default if no specific fragment
                showDashboardView() // Show dashboard content
            }
        } else if (supportFragmentManager.backStackEntryCount == 1) { // Only one fragment left, pop it and show dashboard
            supportFragmentManager.popBackStack()
            setupToolbar("Admin Dashboard", false)
            showDashboardView()
        } else {
            super.onBackPressed() // Perform default back behavior (exit app)
        }
    }

    // Handle ActionBar back button click
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}