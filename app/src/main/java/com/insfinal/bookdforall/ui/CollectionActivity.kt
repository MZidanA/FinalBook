package com.insfinal.bookdforall.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.insfinal.bookdforall.R
import com.insfinal.bookdforall.databinding.ActivityCollectionBinding

class CollectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCollectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCollectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewPagerAndTabs()
        setupBottomNavigation()
    }

    private fun setupViewPagerAndTabs() {
        val fragmentList = arrayListOf<Fragment>(
            LibraryFragment(),
            BookmarksFragment()
        )

        val pagerAdapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = fragmentList.size
            override fun createFragment(position: Int): Fragment = fragmentList[position]
        }

        binding.viewPager.adapter = pagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Perpustakaan"
                1 -> "Bookmarks"
                else -> ""
            }
        }.attach()
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.nav_Collection // Highlight Collection item

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_Collection -> {
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