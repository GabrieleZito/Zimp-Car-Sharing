package com.zimp.zimpcarsharing

import android.content.Intent
import android.os.Bundle
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
            loginUtente(binding.inputUsername.toString(), binding.inputPassword.toString())
        }

    }

    private fun loginUtente(username: String, password: String){
        val query = "Select * from utente where username = $username and password = $password"

        ClientNetwork.retrofit.login(query).enqueue(
            object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful) {
                        if ((response.body()?.get("queryset") as JsonArray).size() == 1) {
                            Toast.makeText(
                                this@LoginActivity,
                                ""+(response.body()?.get("queryset") as JsonArray).get(2),
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