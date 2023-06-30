package com.zimp.zimpcarsharing

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.view.View.OnFocusChangeListener
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.JsonObject
import com.zimp.zimpcarsharing.databinding.ActivityPrenotazioneBinding
import com.zimp.zimpcarsharing.models.Auto
import com.zimp.zimpcarsharing.models.Utente
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class PrenotazioneActivity : AppCompatActivity(), OnMapReadyCallback {
    lateinit var binding: ActivityPrenotazioneBinding
    //lateinit var mapView: MapView
    private val MAPVIEW_BUNDLE_KEY = "MapViewBundleKey"
    private var utente: Utente? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrenotazioneBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.recyclerAuto.layoutManager = LinearLayoutManager(this)
        binding.filterImg.setOnClickListener {
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.filter_dialog)
            dialog.findViewById<Button>(R.id.saveFilter).setOnClickListener {
                val tariffa = dialog.findViewById<Spinner>(R.id.tariffaSpinner)
                val posizione = dialog.findViewById<Spinner>(R.id.posSpinner)
                filtra(tariffa.selectedItem.toString(), posizione.selectedItem.toString())
            }
            dialog.show()
        }
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
        /**
        var mapViewBundle: Bundle? = null
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY)
        }
        mapView = binding.mapViewPrenotazione
        mapView.onCreate(mapViewBundle)
        mapView.getMapAsync(this)
        */
    }

    fun filtra(tariffa: String, posizione: String) {
        Log.i("FILTRO", "$tariffa, $posizione")
    }

    fun avviaMappa(lat: Double, long: Double, binding: ActivityPrenotazioneBinding) {
        Log.i("MAPPA", "$lat, $long")
        var mappa = binding.mapViewPrenotazione
        mappa.visibility = View.VISIBLE
    }

    fun prenotaAuto(auto: Auto, context: Context, utente: Utente?) {
        Log.i("AUTO DA PRENOTARE", "$auto")
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.prenota_dialog)
        dialog.findViewById<TextView>(R.id.autoText).text = "${auto.marca} ${auto.modello}"
        var ore = dialog.findViewById<EditText>(R.id.inputOre)
        ore.onFocusChangeListener = OnFocusChangeListener { _, b ->
            if (!b) {
                dialog.findViewById<TextView>(R.id.costoTotaleText).text = ""+auto.tariffa*(ore.text.toString().toDouble())
            }
        }
        val prenota = dialog.findViewById<Button>(R.id.confermaPrenotBtn)
        prenota.setOnClickListener {
            Toast.makeText(context, "WEEEE", Toast.LENGTH_LONG).show()
        }

        dialog.findViewById<Button>(R.id.confermaPrenotBtn).setOnClickListener {
            val query = "UPDATE zimp_db.auto SET idutente = ${utente?.idUtente}, prenotata = 1 WHERE idauto = ${auto.idAuto}"
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

    override fun onMapReady(map: GoogleMap) {
        map.addMarker(MarkerOptions().position(LatLng(0.0, 0.0)).title("Marker"))

    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        var mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY)
        if (mapViewBundle == null) {
            mapViewBundle = Bundle()
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle)
        }
        //mapView.onSaveInstanceState(mapViewBundle)
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

    override fun onResume() {
        super.onResume()
        //mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        //mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        //mapView.onStop()
    }

    override fun onPause() {
        //mapView.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        //mapView.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        //mapView.onLowMemory()
    }

}