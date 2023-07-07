package com.zimp.zimpcarsharing.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Utente : Serializable{
    @Expose
    @SerializedName("idutente")
    internal var idUtente: Int

    @Expose
    @SerializedName("email")
    internal var email: String

    @Expose
    @SerializedName("username")
    internal var username: String

    @Expose
    @SerializedName("nome")
    internal var nome: String

    @Expose
    @SerializedName("cognome")
    internal var cognome: String

    @Expose
    @SerializedName("password")
    internal var password: String

    @Expose
    @SerializedName("phone")
    internal var phone: String

    constructor(
        idUtente: Int,
        email: String,
        username: String,
        name: String,
        surname: String,
        password: String,
        phone: String
    ) {
        this.idUtente = idUtente
        this.email = email
        this.username = username
        this.nome = name
        this.cognome = surname
        this.password = password
        this.phone = phone
    }

    override fun toString(): String {
        return "Username: $username, Email: $email\n" +
                "Nome: $nome, Cognome: $cognome\n" +
                "PSW: $password, id: $idUtente\n"
    }
}