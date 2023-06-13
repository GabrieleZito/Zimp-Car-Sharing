package com.zimp.zimpcarsharing

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.zimp.zimpcarsharing.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}