package com.zimp.zimpcarsharing.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Auto(

    @Expose
    @SerializedName("idauto")
    internal val idAuto: Int,
    @Expose
    @SerializedName("marca")
    internal val marca: String,
    @Expose
    @SerializedName("modello")
    internal val modello: String,
    @Expose
    @SerializedName("latitudine")
    internal val latitudine: Double,
    @Expose
    @SerializedName("longitudine")
    internal val longitudine: Double,
    @Expose
    @SerializedName("idutente")
    internal val idUtente: Double?,
    @Expose
    @SerializedName("prenotata")
    internal val prenotata: Int,
    @Expose
    @SerializedName("tariffa")
    internal val tariffa: Double,
    @Expose
    @SerializedName("idproprietario")
    internal val idproprietario: Int,
    @Expose
    @SerializedName("orePrenotata")
    internal val orePrenotata: Int
) : Serializable {

    override fun toString(): String {
        return "IdAuto: $idAuto, Marca: $marca" + "Modello: $modello\n" +
                " Lat: $latitudine" + " Long: $longitudine, idUtente: $idUtente\n" +
                "Prenotata: $prenotata,  Tariffa: $tariffa, idProprietario: $idproprietario\n" +
                "Ore Prenotata: $orePrenotata"
    }
}