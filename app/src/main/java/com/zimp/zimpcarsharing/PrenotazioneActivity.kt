package com.zimp.zimpcarsharing

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.zimp.zimpcarsharing.databinding.ActivityPrenotazioneBinding

class PrenotazioneActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPrenotazioneBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /**
        setContentView(R.layout.activity_prenotazione)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, PrenotazioneFragment.newInstance())
                .commitNow()
        }
        */
        binding = ActivityPrenotazioneBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (savedInstanceState == null){
            val manager = supportFragmentManager
            val transaction = manager.beginTransaction()
            transaction.add(R.id.fragmentPrenotazione, PrenotazioneFragment())
            transaction.commit()
        }
    }
}