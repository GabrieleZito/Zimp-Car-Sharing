package com.zimp.zimpcarsharing

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonObject
import com.zimp.zimpcarsharing.databinding.ActivityPrenotazioneBinding
import retrofit2.Callback
import retrofit2.Call
import retrofit2.Response
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.zimp.zimpcarsharing.models.Auto


class PrenotazioneActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPrenotazioneBinding
    var gson : Gson = Gson()
    lateinit var autos : ArrayList<Auto>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrenotazioneBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.recyclerAuto.layoutManager=LinearLayoutManager(this)
        binding.filterImg.setOnClickListener{
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.filter_dialog)
            dialog.findViewById<Button>(R.id.saveFilter).setOnClickListener {
                Log.i("DIALOG", "CIAO")
            }
            dialog.show()
        }
        var data = ArrayList<Auto>()
        /**
        for (i in 1..20){
            data.add(Auto(i, "Patata ", "Cesso", 23.3, 34.2, null, 1, 12.0))
        }
        */
        val extras:Bundle? = intent.extras
        if (extras!=null){
            Log.i("DIO", "quantita: ${extras.getInt("quantita")}")
            for (i in  0 until extras.getInt("quantita")) //Log.i("DIO", "i: $i")
                data.add(extras.getSerializable("Auto $i", Auto::class.java)!!)
        }else
            Toast.makeText(this@PrenotazioneActivity, "Non ci sono auto", Toast.LENGTH_LONG).show()

        for (i in data) Log.i("AUTO", "$i")
        val adapter = AutoAdapter(data)

        binding.recyclerAuto.adapter=adapter
    }





}