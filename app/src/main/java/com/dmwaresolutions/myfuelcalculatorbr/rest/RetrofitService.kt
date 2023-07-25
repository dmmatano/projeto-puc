package com.dmwaresolutions.myfuelcalculatorbr.rest

import com.dmwaresolutions.myfuelcalculatorbr.model.FipeVehicle
import com.dmwaresolutions.myfuelcalculatorbr.model.Make
import com.dmwaresolutions.myfuelcalculatorbr.model.Models
import com.dmwaresolutions.myfuelcalculatorbr.model.Year
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * FIPE API HTTP REST
 * https://deividfortuna.github.io/fipe/
 * */

interface RetrofitService {

    @GET("{veiculo}/marcas")
    fun getAllMakes(@Path("veiculo") veiculo: String): Call<List<Make>>

    @GET("{veiculo}/marcas/{codmarca}/modelos")
    fun getAllModels(
        @Path("veiculo") veiculo: String,
        @Path("codmarca") codMarca: String
    ): Call<Models>

    @GET("{veiculo}/marcas/{codmarca}/modelos/{codmodelo}/anos")
    fun getAllYears(
        @Path("veiculo") veiculo: String,
        @Path("codmarca") codMarca: String,
        @Path("codmodelo") codModelo: String
    ): Call<List<Year>>

    @GET("{veiculo}/marcas/{codmarca}/modelos/{codmodelo}/anos/{codano}")
    fun getFipeVehicle(
        @Path("veiculo") veiculo: String,
        @Path("codmarca") codMarca: String,
        @Path("codmodelo") codModelo: String,
        @Path("codano") codAno: String
    ): Call<FipeVehicle>

    companion object {
        private val retrofitService: RetrofitService by lazy {
            val retrofitService = Retrofit.Builder()
                .baseUrl("https://parallelum.com.br/fipe/api/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            retrofitService.create(RetrofitService::class.java)
        }

        fun getInstance(): RetrofitService {
            return retrofitService
        }
    }

}