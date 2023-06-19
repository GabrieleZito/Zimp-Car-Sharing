package com.zimp.zimpcarsharing

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.zimp.zimpcarsharing.models.Utente
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url

interface UserAPI {
    @POST("postSelect/") // POST per effettuare una SELECT nel database che ha come input una query stringa e restituisce un JSON
    @FormUrlEncoded
    fun login(@Field("query") query: String): Call<JsonObject>

    @POST("postSelect/")
    @FormUrlEncoded
    fun login2(@Field("query") query: String): Call<ResponseBody>

    @POST("postInsert/")
    @FormUrlEncoded
    fun insert(@Field("query") query: String): Call<JsonObject>

    @POST("postUpdate/")
    @FormUrlEncoded
    fun modifica(@Field("query") query: String): Call<JsonObject>

    @POST("postRemove/")
    @FormUrlEncoded
    fun remove(@Field("query") query: String): Call<JsonObject>

    @GET // prende in input un URL e restituisce un ResponseBody
    fun getAvatar(@Url url: String): Call<JsonObject>

}