package com.example.roomsqlitetest.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single

@Dao
interface UserDao {

    @Query("SELECT * FROM users")
    fun getAllUserBlocking(): List<User>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: User)

    @Delete
    fun delete(user: User)

    @Query("SELECT * FROM users WHERE age > :minAge")
    fun getUsersOver(minAge: Int): List<User>

    @Query("SELECT * FROM users WHERE age > :minAge")
    suspend fun getUsersOverSuspend(minAge: Int): List<User>

    //------------------------------------

    @Query("SELECT * FROM users")
    fun getAllUsersOnceRx(): Single<List<User>>

    @Query("SELECT * FROM users")
    fun observeAllUsersRx(): Flowable<List<User>>

    // @Upsert 結合了 @Insert + @Update
    @Upsert
    fun insertRx(user: User): Completable

    @Delete
    fun deleteRx(user: User): Completable

    @Query("SELECT * FROM users WHERE age > :minAge")
    fun getUsersOverRxSingle(minAge: Int): Single<List<User>>

    @Query("SELECT * FROM users WHERE age > :minAge")
    fun getUsersOverRxMaybe(minAge: Int): Maybe<List<User>>
}