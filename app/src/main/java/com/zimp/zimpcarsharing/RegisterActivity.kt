package com.zimp.zimpcarsharing

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.zimp.zimpcarsharing.databinding.RegisterBinding

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: RegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}