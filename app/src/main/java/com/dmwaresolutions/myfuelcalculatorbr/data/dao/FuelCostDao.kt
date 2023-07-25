package com.dmwaresolutions.myfuelcalculatorbr.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dmwaresolutions.myfuelcalculatorbr.model.FuelCost

@Dao
interface FuelCostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cost: FuelCost)

    @Delete
    suspend fun delete(cost: FuelCost)

    @Update
    suspend fun update(cost: FuelCost)

    @Query("SELECT * FROM costs ORDER BY cid DESC")
    fun getAllCosts(): LiveData<List<FuelCost>>
}