package com.dmwaresolutions.myfuelcalculatorbr.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "costs")
@Parcelize
data class FuelCost(
    @PrimaryKey(autoGenerate = true)
    var cid: Int,
    @ColumnInfo(name = "name")
    var name: String,
    @ColumnInfo(name = "total_cost")
    var totalCost: Double,
    @ColumnInfo(name = "vehicle")
    var vehicle: String,
    @ColumnInfo(name = "fuel")
    var fuel: String,
    @ColumnInfo(name = "total_distance")
    var totalDistance: Double
) : Parcelable
