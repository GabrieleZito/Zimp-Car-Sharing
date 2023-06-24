package com.zimp.zimpcarsharing

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.zimp.zimpcarsharing.databinding.ActivityMainBinding
import com.zimp.zimpcarsharing.models.Utente

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val utente:Utente?
        val extras:Bundle? = intent.extras
        if (extras!= null){
            utente = extras.getSerializable("utente", Utente::class.java)
        }else {
            utente = null
            TODO("RIMANDARE AL LOGIN")
        }
        //binding.testo.text = ""+utente
        binding.imgAccount.setOnClickListener {
            val i = Intent(this, AccountActivity::class.java)
            i.putExtra("utente", utente)
            startActivity(i)
        }

        binding.imgLogout.setOnClickListener {
            val i = Intent(this, LoginActivity::class.java)
            startActivity(i)
        }
    }
}