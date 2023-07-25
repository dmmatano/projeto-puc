package com.dmwaresolutions.myfuelcalculatorbr.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "vehicles")
@Parcelize
data class Vehicle(
    @PrimaryKey(autoGenerate = true)
    var vid: Int,
    @ColumnInfo(name = "name")
    var name: String,
    @ColumnInfo(name = "fuel")
    var fuel: String,
    @ColumnInfo(name = "autonomy")
    var autonomy: Double,
    @ColumnInfo(name = "image")
    var image: String?
) : Parcelable