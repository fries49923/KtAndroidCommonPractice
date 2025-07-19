package com.example.roomsqlitetest.room

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single

@Dao
interface ContactInfoDao {

    @Upsert
    fun insertRx(info: ContactInfo): Completable

    @Query("SELECT * FROM contact_infos WHERE userId = :userId")
    fun getContactRxSingle(userId: Int): Single<ContactInfo>

    @Query("SELECT * FROM contact_infos WHERE userId = :userId")
    fun getContactRxMaybe(userId: Int): Maybe<ContactInfo>
}