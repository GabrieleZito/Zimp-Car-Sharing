package com.zimp.zimpcarsharing

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.zimp.zimpcarsharing.databinding.RegisterBinding
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: RegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.inviaRegister.setOnClickListener {
            val username = binding.inputUsernameRegister.text.toString()
            val email = binding.inputEmail.text.toString()
            val pass = binding.inputPasswordRegister.text.toString()
            val nome = binding.inputName.text.toString()
            val cognome = binding.inputSurname.text.toString()
            val phone = binding.inputPhone.text.toString()
            Log.i("MESSAGGIO", username)
            if(email == "" || username == "" || pass == "" || nome == "" || cognome == "" || phone == ""){
                Toast.makeText(
                    this@RegisterActivity,
                    "Inserisci tutti i dati",
                    Toast.LENGTH_LONG
                ).show()
            }else registraUtente(email, username, pass, nome, cognome, phone)

        }
    }

    fun registraUtente(email: String, username: String, pass: String, nome: String, cognome: String, phone: String){
        val query = "Insert into zimp_db.utente (email, username, password, nome, cognome, phone) values ('$email', '$username', '$pass', '$nome', '$cognome', '$phone')"
        Log.i("MESSAGGIO", query)
        ClientNetwork.retrofit.insert(query).enqueue(
            object: Callback<JsonObject>{
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    Log.i("MESSAGGIO", "RESPONSE: $response")

                    if(response.isSuccessful){

                        Toast.makeText(
                            this@RegisterActivity,
                            "Account Creato",
                            Toast.LENGTH_LONG
                        ).show()
                        val i = Intent(this@RegisterActivity, MainActivity::class.java)
                        startActivity(i)
                    }else
                        Toast.makeText(
                            this@RegisterActivity,
                            "Username o Email già presi",
                            Toast.LENGTH_LONG
                        ).show()
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Impossibile connettersi al server",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        )
    }

}