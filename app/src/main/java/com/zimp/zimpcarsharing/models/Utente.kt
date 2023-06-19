package com.zimp.zimpcarsharing.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Utente {
    @Expose
    @SerializedName("email")
    private var email: String
        get() = field
        set(value) {field = value}

    @Expose
    @SerializedName("username")
    private var username: String
        get() = field
        set(value) {field = value}

    @Expose
    @SerializedName("nome")
    private var nome: String
        get() = field
        set(value) {field = value}

    @Expose
    @SerializedName("cognome")
    private var cognome: String
        get() = field
        set(value) {field = value}

    @Expose
    @SerializedName("password")
    private var password: String
        get() = field
        set(value) {field = value}

    @Expose
    @SerializedName("success")
    private var success: Boolean
        get() = field
        set(value) {field = value}

    @Expose
    @SerializedName("message")
    private var message: String
        get() = field
        set(value) {field = value}

    @Expose
    @SerializedName("phone")
    private var phone: String
        get() = field
        set(value) {field = value}
    constructor(
        email: String,
        username: String,
        name: String,
        surname: String,
        password: String,
        success: Boolean,
        message: String,
        phone: String
    ) {
        this.email = email
        this.username = username
        this.nome = name
        this.cognome = surname
        this.password = password
        this.success = success
        this.message = message
        this.phone = phone
    }

    override fun toString(): String {
        return "Username: $username, Email: $email\n" +
                "Nome: $nome, Cognome: $cognome" +
                "PSW: $password"
    }
}