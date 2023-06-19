package com.zimp.zimpcarsharing

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.zimp.zimpcarsharing.databinding.LoginBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: LoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.notRegisteredBtn.setOnClickListener {
            val i = Intent(this, RegisterActivity::class.java)
            i.putExtra("msg", "CIAO")
            startActivity(i)
        }

        binding.inviaLogin.setOnClickListener {
            //Log.i("MESSAGGIO", "CLICCATO LOGIN")
            loginUtente(binding.inputUsername.text.toString(), binding.inputPassword.text.toString())
        }

    }

    private fun loginUtente(username: String, password: String){
        val query = "Select * from zimp_db.utente where username = '$username' and password = '$password'"

        val query1 = "Select * from zimp_db.utente"
        Log.i("MESSAGGIO", "Query: $query")
        Log.i("MESSAGGIO", "Query1: $query1")
        Log.i("MESSAGGIO", "DENTRO LOGIN UTENTE")
        ClientNetwork.retrofit.login(query).enqueue(
            object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {

                    Log.i("MESSAGGIO", "RESPONSE: $response")
                    if (response.isSuccessful) {
                        Log.i("MESSAGGIO", "RESPONSE BODY: ${response.body()}")
                        if ((response.body()?.get("queryset") as JsonArray).size() == 1) {
                            Toast.makeText(
                                this@LoginActivity,
                                "FATTTO",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Toast.makeText(
                                this@LoginActivity,
                                "credenziali errate",
                                Toast.LENGTH_LONG
                            ).show()
                            //binding.progressBar.visibility = View.GONE
                        }
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Log.i("MESSAGGIO", "DENTRO ONFAILURE")
                    Toast.makeText(
                        this@LoginActivity,
                        "Impossibile connettersi al server",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        )

    }

}