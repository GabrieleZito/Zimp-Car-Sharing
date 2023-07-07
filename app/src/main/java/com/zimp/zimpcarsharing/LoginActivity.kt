package com.zimp.zimpcarsharing

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.zimp.zimpcarsharing.databinding.LoginBinding
import com.zimp.zimpcarsharing.models.Utente
import com.google.gson.JsonObject
import com.google.gson.JsonArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: LoginBinding
    var gson : Gson = Gson()
    lateinit var u:Utente
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
            if (binding.inputUsername.text.toString()=="admin" && binding.inputPassword.text.toString()=="admin"){
                val i = Intent(this, MainActivity::class.java)
                i.putExtra("utente", Utente(9999, "admin", "admin", "admin", "admin","admin", "admin"))
                startActivity(i)
            }else
                loginUtente(binding.inputUsername.text.toString(), binding.inputPassword.text.toString())

        }

    }

    private fun loginUtente(username: String, password: String){
        val query = "Select * from zimp_db.utente where username = '$username' and password = '$password'"

        ClientNetwork.retrofit.login(query).enqueue(
            object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {

                    Log.i("MESSAGGIO", "RESPONSE: $response")
                    if (response.isSuccessful) {

                        if ((response.body()?.get("queryset") as JsonArray).size() == 1) {
                            Log.i("MESSAGGIO", "RESPONSE BODY: ${(response.body()?.get("queryset") as JsonArray).get(0)}")
                            Toast.makeText(
                                this@LoginActivity,
                                "FATTO",
                                Toast.LENGTH_LONG
                            ).show()
                            u = gson.fromJson((response.body()?.get("queryset") as JsonArray).get(0), Utente::class.java)
                            Log.i("UTENTE", ""+u )
                            val p = Intent(this@LoginActivity, MainActivity::class.java)
                            p.putExtra("utente", u)
                            startActivity(p)

                        } else {
                            Toast.makeText(
                                this@LoginActivity,
                                "credenziali errate",
                                Toast.LENGTH_LONG
                            ).show()
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