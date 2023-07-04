package com.zimp.zimpcarsharing

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View.OnFocusChangeListener
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.zimp.zimpcarsharing.databinding.ActivityPrenotazioneBinding
import com.zimp.zimpcarsharing.models.Auto
import com.zimp.zimpcarsharing.models.Utente
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class PrenotazioneActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPrenotazioneBinding
    private var utente: Utente? = null
    var gson : Gson = Gson()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrenotazioneBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.recyclerAuto.layoutManager = LinearLayoutManager(this)

        var data = ArrayList<Auto>()
        val extras: Bundle? = intent.extras
        if (extras != null) {
            //Log.i("DIO", "quantita: ${extras.getInt("quantita")}")
            for (i in 0 until extras.getInt("quantita")) //Log.i("DIO", "i: $i")
                data.add(extras.getSerializable("Auto $i", Auto::class.java)!!)
            utente = extras.getSerializable("utente", Utente::class.java)
            Log.i("UTENTE dentro prenotazioneActivity", "$utente")
        } else
            Toast.makeText(this@PrenotazioneActivity, "Non ci sono auto", Toast.LENGTH_LONG).show()

        //for (i in data) Log.i("AUTO", "$i")
        val adapter = AutoAdapter(data, this, binding, utente)

        binding.recyclerAuto.adapter = adapter

        binding.filterImg.setOnClickListener {
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.filter_dialog)
            dialog.findViewById<Button>(R.id.saveFilter).setOnClickListener {
                val filtro = dialog.findViewById<Spinner>(R.id.filtroSpinner)
                filtra(filtro.selectedItem.toString(), adapter, data)
                dialog.hide()
            }
            dialog.show()
        }

    }

    fun filtra(filtro: String, adapter: AutoAdapter, data: ArrayList<Auto>) {
        var query:String = ""
        if (filtro=="Prezzo: Crescente"){
            query = "SELECT * FROM zimp_db.auto WHERE prenotata = 0 ORDER BY tariffa ASC"
        }else if (filtro == "Prezzo: Decrescente"){
            query = "SELECT * FROM zimp_db.auto WHERE prenotata = 0 ORDER BY tariffa DESC"
        }else if (filtro == "Posizione: Più vicino"){
            Log.i("FILTRO", "Posizione vicino")
            query = "SELECT * FROM zimp_db.auto WHERE prenotata = 0 ORDER BY tariffa DESC"
        }else if (filtro == "Posizione: Più lontano"){
            Log.i("FILTRO", "Posizione lontano")
            query = "SELECT * FROM zimp_db.auto WHERE prenotata = 0 ORDER BY tariffa ASC"
        }

        ClientNetwork.retrofit.select(query).enqueue(
            object: Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful){
                        Log.i("RESPONSE", "${response.body()}")
                        for ((i, auto) in (response.body()?.get("queryset") as JsonArray).withIndex()){
                            data[i] = gson.fromJson(auto, Auto::class.java)
                            adapter.notifyItemChanged(i)
                        }
                    }else{
                        Toast.makeText(this@PrenotazioneActivity, "Ops, qualcosa è andato storto", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Toast.makeText(this@PrenotazioneActivity, "Problema di connessione al server", Toast.LENGTH_SHORT).show()
                }
            }
        )

    }

    fun prenotaAuto(auto: Auto, context: Context, utente: Utente?) {
        Log.i("AUTO DA PRENOTARE", "$auto")
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.prenota_dialog)
        dialog.findViewById<TextView>(R.id.autoText).text = "${auto.marca} ${auto.modello}"
        var ore = dialog.findViewById<EditText>(R.id.inputOre)
        ore.onFocusChangeListener = OnFocusChangeListener { _, b ->
            if (!b) {
                dialog.findViewById<TextView>(R.id.costoTotaleText).text = ""+auto.tariffa*(ore.text.toString().toInt())
            }
        }
        val prenota = dialog.findViewById<Button>(R.id.confermaPrenotBtn)

        prenota.setOnClickListener {
            val query = "UPDATE zimp_db.auto SET idutente = ${utente?.idUtente}, prenotata = 1, orePrenotata=${ore.text.toString().toInt()} WHERE idauto = ${auto.idAuto}"
            Log.i("QUERY", query)
            Log.i("UTENTE", "$utente")
            ClientNetwork.retrofit.insert(query).enqueue(
                object : Callback<JsonObject>{
                    override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                        Log.i("PRENOTAZIONE", "${response}")
                        if (response.isSuccessful){
                            Toast.makeText(context, "Auto prenotata", Toast.LENGTH_LONG).show()
                        }else
                            Toast.makeText(context, "Ops qualcosa è andato storto", Toast.LENGTH_LONG).show()
                    }
                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                        Toast.makeText(context, "Niente connessione al server", Toast.LENGTH_LONG).show()
                    }
                }
            )
        }
        dialog.show()

    }

    fun prenota(auto: Auto, utente: Utente?){
        val query = "UPDATE zimp_db.utente SET idutente = ${utente?.idUtente}, prenotata = 1 WHERE idauto = ${auto.idAuto}"
        Log.i("QUERY", query)
        Log.i("UTENTE", "$utente")
        ClientNetwork.retrofit.modifica(query).enqueue(
            object : Callback<JsonObject>{
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    Log.i("PRENOTAZIONE", "${response.body()}")
                    if (response.isSuccessful){
                        Toast.makeText(this@PrenotazioneActivity, "Auto prenotata", Toast.LENGTH_LONG).show()
                    }else
                        Toast.makeText(this@PrenotazioneActivity, "Ops qualcosa è andato storto", Toast.LENGTH_LONG).show()
                }
                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Toast.makeText(this@PrenotazioneActivity, "Ops qualcosa è andato storto", Toast.LENGTH_LONG).show()
                }
            }
        )
    }

    fun avviaMappa(latitudine: Double, longitudine: Double, context: Context) {
        var i = Intent()
        i.setClass(context, MappaActivity::class.java)
        i.putExtra("lat", latitudine)
        i.putExtra("long", longitudine)
        startActivity(i)
    }

}