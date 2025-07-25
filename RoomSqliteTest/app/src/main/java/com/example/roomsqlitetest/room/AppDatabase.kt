package com.example.roomsqlitetest.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        User::class,
        ContactInfo::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    abstract fun contactInfoDao(): ContactInfoDao
}