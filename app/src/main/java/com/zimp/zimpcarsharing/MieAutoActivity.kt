package com.zimp.zimpcarsharing

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.zimp.zimpcarsharing.databinding.ActivityMieAutoBinding
import com.zimp.zimpcarsharing.models.Auto
import com.zimp.zimpcarsharing.models.Utente
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.zimp.zimpcarsharing.utils.checkSelfPermissionCompat
import com.zimp.zimpcarsharing.utils.requestPermissionsCompat
import com.zimp.zimpcarsharing.utils.shouldShowRequestPermissionRationaleCompat
import com.google.android.material.snackbar.Snackbar
import com.zimp.zimpcarsharing.utils.showSnackbar
import java.util.concurrent.TimeUnit


class MieAutoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMieAutoBinding
    private var utente: Utente? = null
    var gson : Gson = Gson()
    var data = ArrayList<Auto>()
    val GALLERY_REQ_CODE = 1000
    val POSITION_REQ_CODE = 100
    private lateinit var image:ImageButton
    lateinit var layout:ConstraintLayout

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var currentLocation: Location? = null

    var latitude:Double? = 0.0
    var longitude:Double? = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMieAutoBinding.inflate(layoutInflater)
        binding.recyclerAuto2.layoutManager = LinearLayoutManager(this)
        setContentView(binding.root)
        layout = binding.root

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        val extras: Bundle? = intent.extras
        if (extras != null) {
            for (i in 0 until extras.getInt("quantita")){
                data.add(extras.getSerializable("Auto $i", Auto::class.java)!!)
                Log.i("AUTO", "${data[i]}")
            }

            utente = extras.getSerializable("utente", Utente::class.java)
            Log.i("UTENTE", "$utente")
        } else
            Toast.makeText(this@MieAutoActivity, "Non ci sono auto", Toast.LENGTH_LONG).show()

        //for (i in data) Log.i("AUTO", "$i")
        val adapter = AutoAdapter2(data, utente)
        binding.recyclerAuto2.adapter  = adapter
        binding.addAuto.setOnClickListener {
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.add_auto_dialog)
            val marca = dialog.findViewById<EditText>(R.id.inputMarca)
            val modello = dialog.findViewById<EditText>(R.id.inputModello)
            val tariffa = dialog.findViewById<EditText>(R.id.inputTariffa)

            dialog.findViewById<Button>(R.id.addBtn).setOnClickListener{
                if (marca.text.toString() == "" || modello.text.toString() == "" || tariffa.text.toString() == ""){
                    Toast.makeText(this@MieAutoActivity, "Inserisci tutti campi", Toast.LENGTH_LONG).show()
                }else{
                    aggiungi(marca.text.toString(), modello.text.toString(), tariffa.text.toString().toInt())
                    dialog.hide()
                }
            }
            image = dialog.findViewById(R.id.inputImg)
            image.setOnClickListener{
                val i = Intent(Intent.ACTION_PICK)
                i.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                startActivityForResult(i, GALLERY_REQ_CODE)
            }

            dialog.findViewById<ImageButton>(R.id.inputPos).setOnClickListener{
                getCurrentLocation()
            }

            dialog.show()
        }
    }



    private fun getCurrentLocation(){
        if (checkPermissions()){
            if(isLocationEnabled()){
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermission()
                    return
                }
                fusedLocationProviderClient.lastLocation.addOnCompleteListener{ task->
                    val location:Location? = task.result
                    if (location == null){
                        Toast.makeText(this@MieAutoActivity, "PROBLEMA", Toast.LENGTH_SHORT).show()
                    }else{
                        Log.i("POSIZIONE", "$location")
                    }
                    
                }
            }else{
                Toast.makeText(this@MieAutoActivity, "ACCENDI IL GPS", Toast.LENGTH_SHORT).show()
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
        val locationManager:LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == POSITION_REQ_CODE){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this@MieAutoActivity, "Permesso concesso", Toast.LENGTH_SHORT).show()
                getCurrentLocation()
            }else{
                Toast.makeText(this@MieAutoActivity, "Permesso non concesso", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun checkPermissions():Boolean{
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode== RESULT_OK && requestCode==GALLERY_REQ_CODE){
            image.setImageURI(data?.data)
            //EVENTUALE CODICE per inviare l'immagine al server
        }
    }
    fun aggiungi(marca: String, modello:String, tariffa:Int){
        var latitudine = 0.0
        var longitudine = 0.0
        val query = "INSERT INTO zimp_db.auto (marca, modello, latitudine, longitudine, prenotata, tariffa, idproprietario, orePrenotata)" +
                                   " VALUES ('$marca', '$modello', $latitudine, $longitudine, 0, $tariffa, ${utente?.idUtente}, 0)"
        ClientNetwork.retrofit.insert(query).enqueue(
            object : Callback<JsonObject>{
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    Log.i("RESPONSE", "$response")
                    if (response.isSuccessful){
                        Toast.makeText(this@MieAutoActivity, "Auto inserita correttamente", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this@MieAutoActivity, "Ops, qualcosa è andato storto", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Toast.makeText(this@MieAutoActivity, "Problema con il server", Toast.LENGTH_LONG).show()
                }
            }
        )
    }

    fun elimina(auto: Auto){
        val query = if (utente?.idUtente == auto.idproprietario) "DELETE FROM zimp_db.auto WHERE idauto=${auto.idAuto}" else "UPDATE zimp_db.auto SET idutente = null WHERE idauto = ${auto.idAuto}"
        
        ClientNetwork.retrofit.modifica(query).enqueue(
            object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    Log.i("RESPONSE", "$response")
                    if (response.isSuccessful){

                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    TODO("Not yet implemented")
                }
            }
        )
        
    }
}