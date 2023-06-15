package com.zimp.zimpcarsharing.models

import com.google.gson.annotations.SerializedName

class Utente {
    @SerializedName("email")
    private var email: String
        get() = field
        set(value) {field = value}

    @SerializedName("username")
    private var username: String
        get() = field
        set(value) {field = value}

    @SerializedName("name")
    private var name: String
        get() = field
        set(value) {field = value}

    @SerializedName("surname")
    private var surname: String
        get() = field
        set(value) {field = value}

    @SerializedName("password")
    private var password: String
        get() = field
        set(value) {field = value}

    @SerializedName("success")
    private var success: Boolean
        get() = field
        set(value) {field = value}

    @SerializedName("message")
    private var message: String
        get() = field
        set(value) {field = value}

    constructor(
        email: String,
        username: String,
        name: String,
        surname: String,
        password: String,
        success: Boolean,
        message: String
    ) {
        this.email = email
        this.username = username
        this.name = name
        this.surname = surname
        this.password = password
        this.success = success
        this.message = message
    }
}