package com.zimp.zimpcarsharing

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View.OnFocusChangeListener
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.zimp.zimpcarsharing.databinding.ActivityPrenotazioneBinding
import com.zimp.zimpcarsharing.models.Auto
import com.zimp.zimpcarsharing.models.Utente
import com.zimp.zimpcarsharing.utils.SortLocations
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Collections


class PrenotazioneActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPrenotazioneBinding
    private var utente: Utente? = null
    var gson : Gson = Gson()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val POSITION_REQ_CODE = 100
    private lateinit var currentLocation: LatLng
    private lateinit var data: ArrayList<Auto>
    private lateinit var adapter:AutoAdapter
    private var ordine:Int = 0
    private var dialog: Dialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrenotazioneBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.recyclerAuto.layoutManager = LinearLayoutManager(this)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        data = ArrayList()
        val extras: Bundle? = intent.extras

        if (extras != null){
            utente = extras.getSerializable("utente", Utente::class.java)
            Log.i("UTENTE", "$utente")
        }

        adapter = AutoAdapter(data, this, binding, utente)
        fetchAuto()
        binding.recyclerAuto.adapter = adapter

        binding.filterImg.setOnClickListener {
            dialog = Dialog(this)
            dialog!!.setContentView(R.layout.filter_dialog)
            dialog!!.findViewById<Button>(R.id.saveFilter).setOnClickListener {
                val filtro = dialog!!.findViewById<Spinner>(R.id.filtroSpinner)
                filtra(filtro.selectedItem.toString(), adapter, data)
                dialog!!.hide()
            }
            dialog!!.show()
        }
    }

    //Query per recuperare le auto da poter prenotare
    fun fetchAuto(){
        val query = "SELECT * FROM zimp_db.auto Where prenotata = 0"

        ClientNetwork.retrofit.select(query).enqueue(
            object: Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    Log.i("MESSAGGIO","${response.body()}")
                    if (response.isSuccessful){
                        for ((i, auto) in (response.body()?.get("queryset") as JsonArray).withIndex()){
                            var x = gson.fromJson(auto, Auto::class.java)
                            data.add(x)
                            adapter.notifyItemInserted(i)
                        }
                    }else{
                        Toast.makeText(this@PrenotazioneActivity, "Non ci sono auto da noleggiare", Toast.LENGTH_LONG).show()
                    }
                }
                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Toast.makeText(this@PrenotazioneActivity, "Problema di connessione con il server ", Toast.LENGTH_LONG).show()
                }
            }
        )

    }
    private fun filtra(filtro: String, adapter: AutoAdapter, data: ArrayList<Auto>) {
        var query: String

        if (filtro=="Prezzo: Crescente"){
            query = "SELECT * FROM zimp_db.auto WHERE prenotata = 0 ORDER BY tariffa ASC"
            filtraPrezzo(query, adapter, data)
        }else if (filtro == "Prezzo: Decrescente"){
            query = "SELECT * FROM zimp_db.auto WHERE prenotata = 0 ORDER BY tariffa DESC"
            filtraPrezzo(query, adapter, data)
        }else if (filtro == "Posizione: Più vicino"){
            ordine = 1
            getLocation(adapter)
        }else if (filtro =="Posizione: Più lontano"){
            ordine = -1
            getLocation(adapter)
        }


    }

    //Per qualche motivo il recupero della posizione nell'emulatore ha funzionato solo la prima volta, poi ha smesso
    // facendola partire dal telefono però funziona ancora
    private fun ordina(adapter: AutoAdapter){

        if (ordine==1){
            Log.i("ORDINE", "Crescente")
            Collections.sort(data, SortLocations(currentLocation))
            for ((i, auto) in data.withIndex())
                adapter.notifyItemChanged(i)
        }else if (ordine == -1){
            Log.i("ORDINE", "Decrescente")
            Collections.sort(data, SortLocations(currentLocation))
            data.reverse()
            for ((i, auto) in data.withIndex())
                adapter.notifyItemChanged(i)
        }
    }

    private fun filtraPrezzo(query: String, adapter: AutoAdapter, data: ArrayList<Auto>){
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
            if (ore.text.toString() == ""){
                Toast.makeText(context, "Inserisci le ore", Toast.LENGTH_SHORT).show()
            }else{
                val query = "UPDATE zimp_db.auto SET idutente = ${utente?.idUtente}, prenotata = 1, orePrenotata=${ore.text.toString().toInt()} WHERE idauto = ${auto.idAuto}"
                Log.i("QUERY", query)
                Log.i("UTENTE", "$utente")
                ClientNetwork.retrofit.insert(query).enqueue(
                    object : Callback<JsonObject>{
                        override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                            Log.i("PRENOTAZIONE", "${response}")
                            if (response.isSuccessful){
                                Toast.makeText(context, "Auto prenotata", Toast.LENGTH_LONG).show()
                                dialog.dismiss()
                            }else
                                Toast.makeText(context, "Ops qualcosa è andato storto", Toast.LENGTH_LONG).show()
                        }
                        override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                            Toast.makeText(context, "Niente connessione al server", Toast.LENGTH_LONG).show()
                        }
                    }
                )
            }
        }
        dialog.show()
    }

    private fun getLocation(adapter: AutoAdapter) {
        if (checkPermissions()){
            if(isLocationEnabled()){
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestPermission()
                    return
                }
                fusedLocationProviderClient.lastLocation.addOnCompleteListener{ task->
                    val location: Location? = task.result
                    if (location == null){
                        Toast.makeText(this@PrenotazioneActivity, "PROBLEMA a trovare la tua posizione", Toast.LENGTH_SHORT).show()
                    }else{
                        currentLocation = LatLng(location.latitude, location.longitude)
                        Log.i("POSIZIONE", "$location")
                        ordina(adapter)
                    }

                }
            }else{
                Toast.makeText(this@PrenotazioneActivity, "ACCENDI IL GPS", Toast.LENGTH_SHORT).show()
                val int = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(int)
            }
        }else{
            requestPermission()
        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), POSITION_REQ_CODE)
    }
    private fun isLocationEnabled():Boolean{
        val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER)
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == POSITION_REQ_CODE){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this@PrenotazioneActivity, "Permesso concesso", Toast.LENGTH_SHORT).show()
                getLocation(adapter)
            }else{
                Toast.makeText(this@PrenotazioneActivity, "Permesso non concesso", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun checkPermissions():Boolean{
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }


    override fun onStop() {
        super.onStop()
        if (dialog != null) {
            dialog?.dismiss()
            dialog = null
        }
    }
}