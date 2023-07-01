package com.zimp.zimpcarsharing

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.zimp.zimpcarsharing.databinding.ActivityMainBinding
import com.zimp.zimpcarsharing.models.Auto
import com.zimp.zimpcarsharing.models.Utente
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    var gson : Gson = Gson()
    private var utente: Utente? = null
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
        //binding.testo.text = ""+utente
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
            fetchAuto()
        }

        binding.myAutoBtn.setOnClickListener {
            fetchAutoMie()
        }

    }
    fun fetchAuto(){
        val query = "SELECT * FROM zimp_db.auto Where prenotata = 0"

        ClientNetwork.retrofit.select(query).enqueue(
            object: Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    Log.i("MESSAGGIO","${response.body()}")
                    if (response.isSuccessful){
                        val int = Intent(this@MainActivity, PrenotazioneActivity::class.java)
                        int.putExtra("quantita", (response.body()?.get("queryset") as JsonArray).size())
                        for ((i, auto) in (response.body()?.get("queryset") as JsonArray).withIndex()){

                            var x = gson.fromJson(auto, Auto::class.java)
                            //Log.i("Auto", "JSON: $auto")
                            //Log.i("Auto", "AUTO: $x")

                            int.putExtra("Auto $i", x)

                        }
                        int.putExtra("utente", utente)
                        startActivity(int)

                    }else{
                        Toast.makeText(this@MainActivity, "Non ci sono auto da noleggiare", Toast.LENGTH_LONG).show()
                    }
                }
                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Toast.makeText(this@MainActivity, "Problema di connessione con il server ", Toast.LENGTH_LONG).show()
                }
            }
        )

    }

    fun fetchAutoMie(){
        val query = "SELECT * FROM zimp_db.auto WHERE idproprietario=${utente?.idUtente}"
        Log.i("QUERY", query)

        ClientNetwork.retrofit.select(query).enqueue(
            object: Callback<JsonObject>{
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if(response.isSuccessful){
                        Log.i("RESPONSE", "${response.body()}")
                        val int = Intent(this@MainActivity, MieAutoActivity::class.java)
                        int.putExtra("quantita", (response.body()?.get("queryset") as JsonArray).size())
                        for ((i, auto) in (response.body()?.get("queryset") as JsonArray).withIndex()){

                            var x = gson.fromJson(auto, Auto::class.java)
                            int.putExtra("Auto $i", x)
                            Log.i("AUTO", "$x")

                        }
                        int.putExtra("utente", utente)
                        startActivity(int)

                    }else
                        Toast.makeText(this@MainActivity, "Ops, qualcosa è andato storto", Toast.LENGTH_LONG).show()
                }
                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Toast.makeText(this@MainActivity, "Problema di connessione con il server ", Toast.LENGTH_LONG).show()

                }
            }
        )
    }

}