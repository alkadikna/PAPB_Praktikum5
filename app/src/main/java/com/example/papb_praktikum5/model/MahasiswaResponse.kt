package com.example.papb_praktikum5.model

import com.google.gson.annotations.SerializedName

class MahasiswaResponse {
    @SerializedName("data")
    val data: List<Mahasiswa>? = null
    @SerializedName("status")
    val status: Boolean = false
}