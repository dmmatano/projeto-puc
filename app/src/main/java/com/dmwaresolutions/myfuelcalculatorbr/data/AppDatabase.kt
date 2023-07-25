package com.dmwaresolutions.myfuelcalculatorbr.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dmwaresolutions.myfuelcalculatorbr.data.dao.FuelCostDao
import com.dmwaresolutions.myfuelcalculatorbr.data.dao.UserDao
import com.dmwaresolutions.myfuelcalculatorbr.data.dao.VehicleDao
import com.dmwaresolutions.myfuelcalculatorbr.model.FuelCost
import com.dmwaresolutions.myfuelcalculatorbr.model.User
import com.dmwaresolutions.myfuelcalculatorbr.model.Vehicle

@Database(
    entities = [FuelCost::class, Vehicle::class, User::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getFuelCostDao(): FuelCostDao
    abstract fun getVehicleDao(): VehicleDao
    abstract fun getUserDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(ctxt: Context) =
            Room.databaseBuilder(
                ctxt.applicationContext,
                AppDatabase::class.java,
                "appdatabase"
            ).fallbackToDestructiveMigration().build()

    }

}
