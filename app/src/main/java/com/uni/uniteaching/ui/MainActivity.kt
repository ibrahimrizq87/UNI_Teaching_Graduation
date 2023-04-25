package com.uni.uniteaching.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.uni.uniteaching.R
import com.uni.uniteaching.databinding.ActivityMainBinding
import com.uni.uniteaching.ui.signUp.SignUp

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.signUp.setOnClickListener {
            startActivity(Intent(this,SignUp::class.java))
        }
        binding.logIn.setOnClickListener {
            startActivity(Intent(this,LogIn::class.java))
    }
}
}