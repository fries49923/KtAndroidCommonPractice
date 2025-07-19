package com.example.roomsqlitetest

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.roomsqlitetest.room.AppDatabase
import com.example.roomsqlitetest.room.User
import com.example.roomsqlitetest.room.UserDao
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class UserDaoTest01 {

    private lateinit var database: AppDatabase
    private lateinit var userDao: UserDao

    @Before // 每個測試執行前都會跑一次
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        userDao = database.userDao()

        // 共通的測試資料
        val alice = User(name = "Alice", age = 20, birthday = "2003.05.20")
        val bob = User(name = "Bob", age = 30, birthday = "1993.09.15")
        userDao.insert(alice)
        userDao.insert(bob)
    }

    @After // 每個測試執行後都會跑一次
    fun teardown() {
        database.close()
    }

    @Test
    fun insert_and_query_user_should_work() {
        val user = User(
            name = "Charlie",
            age = 45,
            birthday = "1978.11.03"
        )

        userDao.insert(user)
        val users = userDao.getAllUserBlocking()

        assertTrue(
            users.any {
                it.name == user.name
                        && it.age == user.age
                        && it.birthday == user.birthday
            })
    }
}