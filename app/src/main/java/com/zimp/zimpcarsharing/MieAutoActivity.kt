package com.zimp.zimpcarsharing

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
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
    private lateinit var data: ArrayList<Auto>
    private lateinit var adapter:AutoAdapter2
    val GALLERY_REQ_CODE = 1000
    val POSITION_REQ_CODE = 100
    private lateinit var image:ImageButton
    lateinit var layout:ConstraintLayout

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var dialog:Dialog? = null
    companion object{
        var latitude:Double? = 0.0
        var longitude:Double? = 0.0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMieAutoBinding.inflate(layoutInflater)
        binding.recyclerAuto2.layoutManager = LinearLayoutManager(this)
        setContentView(binding.root)
        layout = binding.root
        data = ArrayList()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        val extras: Bundle? = intent.extras
        if (extras != null) {
            utente = extras.getSerializable("utente", Utente::class.java)
            Log.i("UTENTE", "$utente")
        } else
            Toast.makeText(this@MieAutoActivity, "Non ci sono auto", Toast.LENGTH_LONG).show()


        adapter = AutoAdapter2(data, utente, this)
        fetchAutoMie()
        binding.recyclerAuto2.adapter  = adapter
        binding.addAuto.setOnClickListener {
            dialog = Dialog(this)
            dialog!!.setContentView(R.layout.add_auto_dialog)
            val marca = dialog!!.findViewById<EditText>(R.id.inputMarca)
            val modello = dialog!!.findViewById<EditText>(R.id.inputModello)
            val tariffa = dialog!!.findViewById<EditText>(R.id.inputTariffa)

            dialog!!.findViewById<Button>(R.id.addBtn).setOnClickListener{
                if (marca.text.toString() == "" || modello.text.toString() == "" || tariffa.text.toString() == ""){
                    Toast.makeText(this@MieAutoActivity, "Inserisci tutti campi", Toast.LENGTH_LONG).show()
                }else{
                    aggiungi(marca.text.toString(), modello.text.toString(), tariffa.text.toString().toInt())
                    dialog!!.hide()
                }
            }
            image = dialog!!.findViewById(R.id.inputImg)
            image.setOnClickListener{
                val i = Intent(Intent.ACTION_PICK)
                i.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                startActivityForResult(i, GALLERY_REQ_CODE)
            }

            dialog!!.findViewById<ImageButton>(R.id.inputPos).setOnClickListener{
                getCurrentLocation()
            }

            dialog!!.show()
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
                        latitude = location.latitude
                        longitude = location.longitude
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

        val query = "INSERT INTO zimp_db.auto (marca, modello, latitudine, longitudine, prenotata, tariffa, idproprietario, orePrenotata)" +
                                   " VALUES ('$marca', '$modello', $latitude, $longitude, 0, $tariffa, ${utente?.idUtente}, 0)"
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

    fun elimina(auto: Auto, context: Context, utente: Utente?){
        val queryElimina = "DELETE FROM zimp_db.auto WHERE idauto=${auto.idAuto}"
        val queryModifica = "UPDATE zimp_db.auto SET idutente = null, prenotata = 0 WHERE idauto = ${auto.idAuto}"

        Log.i("QUERY", "$queryElimina, idUtente: ${utente?.idUtente}, idAuto: ${auto.idAuto}")
        Log.i("QUERY", "$queryModifica")


        if (auto.idproprietario == utente?.idUtente){
            ClientNetwork.retrofit.remove(queryElimina).enqueue(
                object : Callback<JsonObject> {
                    override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                        Log.i("RESPONSE", "$response")
                        if (response.isSuccessful){
                            Toast.makeText(context, "Auto eliminata", Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(context, "Ops, qualcosa è andato storto", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                        Toast.makeText(context, "Problema di connessione al server", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }else{
            ClientNetwork.retrofit.modifica(queryModifica).enqueue(
                object : Callback<JsonObject> {
                    override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                        Log.i("RESPONSE", "$response")
                        if (response.isSuccessful){
                            Toast.makeText(context, "Prenotazione cancellata", Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(context, "Ops, qualcosa è andato storto", Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                        Toast.makeText(context, "Problema di connessione al server", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
        
    }
    override fun onStop() {
        super.onStop()
        if (dialog != null) {
            dialog!!.dismiss()
            dialog = null
        }
    }

    fun fetchAutoMie(){
        val query = "SELECT * FROM zimp_db.auto WHERE idproprietario=${utente?.idUtente} OR idutente=${utente?.idUtente}"
        Log.i("QUERY", query)

        ClientNetwork.retrofit.select(query).enqueue(
            object: Callback<JsonObject>{
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if(response.isSuccessful){
                        Log.i("RESPONSE", "${response.body()}")
                        for ((i, auto) in (response.body()?.get("queryset") as JsonArray).withIndex()){

                            var x = gson.fromJson(auto, Auto::class.java)
                            data.add(x)
                            adapter.notifyItemInserted(i)
                            //Log.i("AUTO", "$x")

                        }
                    }else
                        Toast.makeText(this@MieAutoActivity, "Ops, qualcosa è andato storto", Toast.LENGTH_LONG).show()
                }
                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Toast.makeText(this@MieAutoActivity, "Problema di connessione con il server ", Toast.LENGTH_LONG).show()

                }
            }
        )
    }

}