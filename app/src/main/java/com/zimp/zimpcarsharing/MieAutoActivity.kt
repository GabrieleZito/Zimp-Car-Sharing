package com.zimp.zimpcarsharing

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.zimp.zimpcarsharing.databinding.ActivityMieAutoBinding
import com.zimp.zimpcarsharing.models.Auto
import com.zimp.zimpcarsharing.models.Utente
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MieAutoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMieAutoBinding
    private var utente: Utente? = null
    var gson : Gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMieAutoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var data = ArrayList<Auto>()
        val extras: Bundle? = intent.extras
        if (extras != null) {
            //Log.i("DIO", "quantita: ${extras.getInt("quantita")}")
            for (i in 0 until extras.getInt("quantita")) //Log.i("DIO", "i: $i")
                data.add(extras.getSerializable("Auto $i", Auto::class.java)!!)
            utente = extras.getSerializable("utente", Utente::class.java)
            Log.i("UTENTE", "$utente")
        } else
            Toast.makeText(this@MieAutoActivity, "Non ci sono auto", Toast.LENGTH_LONG).show()

        //for (i in data) Log.i("AUTO", "$i")
        data.add(Auto(4, "ciao", "ciao", 213.0, 213.0, 3, 1, 23.0, 1, 23))
        val adapter = AutoAdapter2(data, utente)
        binding.recyclerAuto2.adapter  = adapter

    }



    fun elimina(auto: Auto){
        Log.i("MESSAGGIO", "Cliccato elimina di ${auto.modello}")
    }
}