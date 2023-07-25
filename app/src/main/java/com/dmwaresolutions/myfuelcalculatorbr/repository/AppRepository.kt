package com.dmwaresolutions.myfuelcalculatorbr.repository

import com.dmwaresolutions.myfuelcalculatorbr.data.AppDatabase
import com.dmwaresolutions.myfuelcalculatorbr.model.FuelCost
import com.dmwaresolutions.myfuelcalculatorbr.model.User
import com.dmwaresolutions.myfuelcalculatorbr.model.Vehicle
import com.dmwaresolutions.myfuelcalculatorbr.rest.RetrofitService

class AppRepository(
    private val database: AppDatabase,
    private val retrofitService: RetrofitService
) {

    // Vehicle
    suspend fun insertVehicle(vehicle: Vehicle) = database.getVehicleDao().insert(vehicle)

    suspend fun deleteVehicle(vehicle: Vehicle) = database.getVehicleDao().delete(vehicle)

    suspend fun updateVehicle(vehicle: Vehicle) = database.getVehicleDao().update(vehicle)

    fun getAllVehicles() = database.getVehicleDao().getAllVehicles()

    //FuelCost
    suspend fun insertCost(cost: FuelCost) = database.getFuelCostDao().insert(cost)

    suspend fun deleteCost(cost: FuelCost) = database.getFuelCostDao().delete(cost)

    fun getAllCosts() = database.getFuelCostDao().getAllCosts()

    //User
    suspend fun insertUser(user: User) = database.getUserDao().insert(user)

    suspend fun updateUser(user: User) = database.getUserDao().update(user)

    fun getUser() = database.getUserDao().getUser()

    //FIPE
    fun getAllMakes(veiculo: String) = retrofitService.getAllMakes(veiculo)

    fun getAllModels(veiculo: String, codMarca: String) =
        retrofitService.getAllModels(veiculo, codMarca)

    fun getAllYears(veiculo: String, codMarca: String, codModelo: String) =
        retrofitService.getAllYears(veiculo, codMarca, codModelo)

    fun getFipeVehicle(veiculo: String, codMarca: String, codModelo: String, codAno: String) =
        retrofitService.getFipeVehicle(veiculo, codMarca, codModelo, codAno)


}