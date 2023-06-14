package com.zimp.zimpcarsharing

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.gson.JsonObject
import com.zimp.zimpcarsharing.databinding.LoginBinding
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

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
    }

    private fun loginUtente(){
        val query = "Select * from persona"
        /**
        ClientNetwork.retrofit.login(query).enqueue(

            object : Callback <JsonObject>{
                fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>){
                    
                }
                fun onFailure(call: Call<JsonObject>, t : Throwable){

                }
            }

        )
        */
    }

}