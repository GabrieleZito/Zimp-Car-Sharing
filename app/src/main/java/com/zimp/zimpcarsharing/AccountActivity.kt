package com.zimp.zimpcarsharing

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.JsonObject
import com.zimp.zimpcarsharing.databinding.ActivityAccountBinding
import com.zimp.zimpcarsharing.models.Utente
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AccountActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAccountBinding
    private var utente: Utente? = null

    private lateinit var username : EditText
    private lateinit var name     : EditText
    private lateinit var surname  : EditText
    private lateinit var phone    : EditText
    private lateinit var pass     : EditText
    private var salvato:Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        username= binding.editUsername
        name = binding.editName
        surname = binding.editSurname
        phone = binding.editPhone
        pass = binding.editPassword

        val extras:Bundle? = intent.extras
        if (extras!= null){
            utente = extras.getSerializable("utente", Utente::class.java)
            username.setText(utente?.username)
            name.setText(utente?.nome)
            surname.setText(utente?.cognome)
            phone.setText(utente?.phone)
            pass.setText((utente?.password))
        }else {
            utente = null
            val i = Intent(this, LoginActivity::class.java)
            startActivity(i)
        }

        binding.accountSave.setOnClickListener {
            inviaModifica()
        }
    }

    private fun inviaModifica(){
        val query = "UPDATE zimp_db.utente SET username = '${username.text}', nome = '${name.text}', cognome = '${surname.text}', phone = '${phone.text}', password = '${pass.text}' WHERE idutente = ${utente!!.idUtente}"
        ClientNetwork.retrofit.insert(query).enqueue(
            object : Callback<JsonObject>{
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful){
                        Toast.makeText(this@AccountActivity, "Modifica Effettuata", Toast.LENGTH_LONG).show()
                        utente!!.username = username.text.toString()
                        utente!!.nome = name.text.toString()
                        utente!!.cognome = surname.toString()
                        utente!!.phone = phone.toString()
                        utente!!.password = pass.toString()
                        salvato = true
                    }else{
                        Toast.makeText(this@AccountActivity, "Qualcosa è andato storto", Toast.LENGTH_LONG).show()
                    }
                }
                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Toast.makeText(this@AccountActivity, "Problema con la connessione al server", Toast.LENGTH_LONG).show()
                }
            }
        )

    }

    override fun onBackPressed() {
        // TODO:  https://developer.android.com/guide/navigation/custom-back

        if (!checkInput(utente) || salvato) {
            super.onBackPressed()
        } else {
            AlertDialog.Builder(this)
                .setMessage("Uscire senza salvare?")
                .setCancelable(false)
                .setPositiveButton("Si") { _, _ -> super.onBackPressed() }
                .setNegativeButton("No", null)
                .show()
        }
    }

    private fun checkInput(utente: Utente?):Boolean{
        return ((binding.editUsername.text.toString() != utente?.username) ||
                (binding.editName.text.toString() != utente.nome) ||
                (binding.editSurname.text.toString() != utente.cognome) ||
                (binding.editPhone.text.toString() != utente.phone) ||
                (binding.editPassword.text.toString() != utente.password))
    }
}