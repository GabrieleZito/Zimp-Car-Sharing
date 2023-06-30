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
        if (extras!=null){
            utente = extras.getSerializable("utente", Utente::class.java)
        }
        fetchAutoMie(data)
        val adapter = AutoAdapter2(data, utente)
        binding.recyclerAuto2.adapter = adapter
    }

    fun fetchAutoMie(data: ArrayList<Auto>){
        val query = "SELECT * FROM zimp_db.auto WHERE idproprietario=${utente?.idUtente}"
        Log.i("QUERY", query)

        ClientNetwork.retrofit.select(query).enqueue(
            object: Callback<JsonObject>{
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if(response.isSuccessful){
                        Log.i("RESPONSE", "${response.body()}")
                        for ((i, auto) in (response.body()?.get("queryset") as JsonArray).withIndex()){

                            var x = gson.fromJson(auto, Auto::class.java)
                            Log.i("AUTO", "$x")
                            data.add(x)


                        }
                    }else
                        Toast.makeText(this@MieAutoActivity, "Ops, qualcosa è andato storto", Toast.LENGTH_LONG).show()
                }
                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    TODO("Not yet implemented")
                }
            }
        )
    }

    fun elimina(auto: Auto){
        Log.i("MESSAGGIO", "Cliccato elimina di ${auto.modello}")
    }
}