package com.example.roomsqlitetest.room

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

@Dao
interface ContactInfoDao {

    @Upsert
    fun insert(info: ContactInfo): Completable

    @Query("SELECT * FROM contact_infos WHERE userId = :userId")
    fun getContactRx(userId: Int): Single<ContactInfo>
}