package com.insfinal.bookdforall.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.booksforall.databinding.ActivitySplashBinding // Corrected package for generated binding class

class SplashActivity : AppCompatActivity() { //

    private val SPLASH_TIME_OUT: Long = 3000 // 3 seconds
    private lateinit var binding: ActivitySplashBinding //

    override fun onCreate(savedInstanceState: Bundle?) { //
        super.onCreate(savedInstanceState) //
        binding = ActivitySplashBinding.inflate(layoutInflater) //
        setContentView(binding.root) //

        // Using a Handler to delay the transition to the next activity
        Handler(Looper.getMainLooper()).postDelayed({ //
            // This method will be executed once the timer is over
            // Start your app main activity
            val intent = Intent(this, LoginActivity::class.java) // You might navigate to MainActivity if user is already logged in
            startActivity(intent) //

            // close this activity
            finish() //
        }, SPLASH_TIME_OUT) //
    }
}