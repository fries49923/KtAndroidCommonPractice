package com.example.roomsqlitetest.di

import android.content.Context
import androidx.room.Room
import com.example.roomsqlitetest.room.AppDatabase
import com.example.roomsqlitetest.room.ContactInfoDao
import com.example.roomsqlitetest.room.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_database.db"
        ).build()
    }

    @Provides
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    fun provideContactInfoDao(database: AppDatabase): ContactInfoDao {
        return database.contactInfoDao()
    }
}