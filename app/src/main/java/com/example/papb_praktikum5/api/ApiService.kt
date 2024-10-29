package com.example.papb_praktikum5.api

import android.telecom.Call
import com.example.papb_praktikum5.model.AddMahasiswaResponse
import com.example.papb_praktikum5.model.DeleteMahasiswaResponse
import com.example.papb_praktikum5.model.Mahasiswa
import com.example.papb_praktikum5.model.MahasiswaResponse
import com.example.papb_praktikum5.model.UpdateMahasiswaResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query


interface ApiService {
    @GET("mahasiswa")
    suspend fun getMahasiswa(@Query("nrp") nrp: String?): Response<MahasiswaResponse?>

    @GET("mahasiswa")
    suspend fun getAllMahasiswa(): Response<MahasiswaResponse>

    @FormUrlEncoded
    @POST("mahasiswa")
    suspend fun addMahasiswa(
        @Field("nrp") nrp: String?,
        @Field("nama") nama: String?,
        @Field("email") email: String?,
        @Field("jurusan") jurusan: String?
    ): Response<AddMahasiswaResponse?>

    @FormUrlEncoded
    @PUT("mahasiswa")
    suspend fun updateMahasiswa(
        @Field("id") id: String?,
        @Field("nrp") nrp: String?,
        @Field("nama") nama: String?,
        @Field("email") email: String?,
        @Field("jurusan") jurusan: String?
    ): Response<UpdateMahasiswaResponse?>

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "mahasiswa", hasBody = true)
    suspend fun deleteMahasiswa(
        @Field("id") id: String?
    ): Response<DeleteMahasiswaResponse>
}
