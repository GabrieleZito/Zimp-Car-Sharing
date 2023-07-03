package com.zimp.zimpcarsharing

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnCompleteListener
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
            fetchAuto()
        }

        binding.myAutoBtn.setOnClickListener {
            fetchAutoMie()
        }
        //getLastKnownLocation()
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
        val query = "SELECT * FROM zimp_db.auto WHERE idproprietario=${utente?.idUtente} OR idutente=${utente?.idUtente}"
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
/**
    fun getLastKnownLocation(){
        Log.i("MESSAGGIO", "getLastKnownLocation chiamata")
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 300)

        }
        Log.i("MESSAGGIO", "getLastKnownLocation chiamata dopo")
        locationProviderClient.lastLocation
            .addOnCompleteListener { task ->
                Log.i("MESSAGGIO", "DEntro complete listener")
                if (task.isSuccessful) {
                    val location = task.result
                    //val geoPoint = GeoPoint(location.latitude, location.longitude)

                    //Log.d("Location", "onComplete: latitude: " + location.latitude)
                    //Log.d("Location", "onComplete: longitude: " + location.longitude)
                }
            }
    }
    */

}