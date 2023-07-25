package com.dmwaresolutions.myfuelcalculatorbr.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "users")
@Parcelize
data class User(
    @PrimaryKey(autoGenerate = true)
    var uid: Int,
    @ColumnInfo(name = "name")
    var name: String
) : Parcelable
