package com.hg.crs.demo07.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    // 加上suspend使它成为挂起函数，编译器会自动加上协程支持
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)

    // 返回的是Flow编译器则会自动加上协程支持
    @Query("SELECT * FROM user")
    fun getAll(): Flow<List<User>>

}