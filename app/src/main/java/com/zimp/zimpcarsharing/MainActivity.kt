package com.zimp.zimpcarsharing

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import com.zimp.zimpcarsharing.databinding.ActivityMainBinding
import com.zimp.zimpcarsharing.models.Utente

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    var gson : Gson = Gson()
    private var utente: Utente? = null
    private lateinit var locationProviderClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val extras:Bundle? = intent.extras
        if (extras!= null){
            utente = extras.getSerializable("utente", Utente::class.java)
        }else {
            val i = Intent(this, LoginActivity::class.java)
            utente = null
            startActivity(i)
        }
        if (utente == null){
            val i = Intent(this, LoginActivity::class.java)
            startActivity(i)
        }


        locationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        binding.accountButton.setOnClickListener {
            val i = Intent(this, AccountActivity::class.java)
            i.putExtra("utente", utente)
            startActivity(i)
        }

        binding.logoutBtn.setOnClickListener {
            val i = Intent(this, LoginActivity::class.java)
            utente = null
            startActivity(i)

        }

        binding.prenotaBtnMain.setOnClickListener {
            val i = Intent(this, PrenotazioneActivity::class.java)
            i.putExtra("utente", utente)
            startActivity(i)
        }

        binding.myAutoBtn.setOnClickListener {
            val i = Intent(this, MieAutoActivity::class.java)
            i.putExtra("utente", utente)
            startActivity(i)
        }

    }

    override fun onBackPressed() {
        utente = null
        super.onBackPressed()
    }


}