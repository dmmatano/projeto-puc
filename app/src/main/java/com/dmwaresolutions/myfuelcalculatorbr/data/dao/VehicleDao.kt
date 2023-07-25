package com.dmwaresolutions.myfuelcalculatorbr.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dmwaresolutions.myfuelcalculatorbr.model.Vehicle

@Dao
interface VehicleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vehicle: Vehicle)

    @Delete
    suspend fun delete(vehicle: Vehicle)

    @Update
    suspend fun update(vehicle: Vehicle)

    @Query("SELECT * FROM vehicles ORDER BY vid DESC")
    fun getAllVehicles(): LiveData<List<Vehicle>>
}