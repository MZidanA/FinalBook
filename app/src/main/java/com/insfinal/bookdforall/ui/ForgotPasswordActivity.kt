package com.insfinal.bookdforall.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.insfinal.booksforall.databinding.ActivityForgotPasswordBinding // Corrected package for generated binding class

class ForgotPasswordActivity : AppCompatActivity() { //

    private lateinit var binding: ActivityForgotPasswordBinding //

    override fun onCreate(savedInstanceState: Bundle?) { //
        super.onCreate(savedInstanceState) //
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater) //
        setContentView(binding.root) //

        binding.buttonForgotPassword.setOnClickListener { //
            val email = binding.editTextEmailForgotPassword.text.toString().trim() //

            if (email.isEmpty()) { //
                Toast.makeText(this, "Please enter your email address", Toast.LENGTH_SHORT).show() //
            } else {
                // Here you would typically implement the logic to send a password reset email.
                // This would involve calling an API or a backend service.
                Toast.makeText(this, "Password reset link sent to $email (if account exists)", Toast.LENGTH_LONG).show() //
                // Optionally, navigate back to the login screen or show a success message.
                // finish()
            }
        }
    }
}