package com.dmwaresolutions.myfuelcalculatorbr.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dmwaresolutions.myfuelcalculatorbr.model.User

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)

    @Delete
    suspend fun delete(user: User)

    @Update
    suspend fun update(user: User)

    @Query("SELECT * FROM users ORDER BY uid DESC")
    fun getUser(): LiveData<List<User>>

    @Query("DELETE FROM users")
    suspend fun obliterate()
}