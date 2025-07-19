package com.example.roomsqlitetest

import android.annotation.SuppressLint
import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.roomsqlitetest.room.AppDatabase
import com.example.roomsqlitetest.room.ContactInfo
import com.example.roomsqlitetest.room.ContactInfoDao
import com.example.roomsqlitetest.room.User
import com.example.roomsqlitetest.room.UserDao
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(RobolectricTestRunner::class)
class ContactInfoTest01 {

    private lateinit var database: AppDatabase
    private lateinit var userDao: UserDao
    private lateinit var contactInfoDao: ContactInfoDao

    @Before // 每個測試執行前都會跑一次
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        userDao = database.userDao()
        contactInfoDao = database.contactInfoDao()

        // 共通的測試資料
        userDao.insert(User(id = 1, name = "Alice", age = 20, birthday = "2003.05.20"))
        userDao.insert(User(id = 2, name = "Bob", age = 30, birthday = "1993.09.15"))

        contactInfoDao.insertRx(
            ContactInfo(
                userId = 1,
                phone = "0988888888",
                email = "alice@test.com"
            )
        ).blockingAwait()

        contactInfoDao.insertRx(
            ContactInfo(
                userId = 2,
                phone = "0977777777",
                email = "bob@test.com"
            )
        ).blockingAwait()
    }

    @After // 每個測試執行後都會跑一次
    fun teardown() {
        database.close()
    }

    @Test
    fun getContactRxSingle_should_succeed01() {
        val latch = CountDownLatch(1)
        var success = false

        val disposable = contactInfoDao.getContactRxSingle(1)
            .subscribe(
                { contactInfo ->
                    success = true
                    latch.countDown()
                },
                {
                    success = false
                    latch.countDown()
                }
            )

        // 最多等 3 秒
        latch.await(3, TimeUnit.SECONDS)
        disposable.dispose()
        assertTrue(success)
    }

    // 可能有非同步問題
    @SuppressLint("CheckResult")
    @Test
    fun getContactRxSingle_should_succeed02() {

        contactInfoDao.getContactRxSingle(1)
            .subscribe(
                { contactInfo ->
                    assertTrue(true)
                },
                {
                    assertTrue(false)
                }
            )
    }

    // 可能有非同步問題
    @SuppressLint("CheckResult")
    @Test
    fun getContactRxSingle_should_failed01() {

        contactInfoDao.getContactRxSingle(100)
            .subscribe(
                { contactInfo ->
                    assertTrue(false)
                },
                {
                    assertTrue(true)
                }
            )
    }

    // 可能有非同步問題
    @SuppressLint("CheckResult")
    @Test
    fun getContactRxMaybe_should_succeed01() {

        contactInfoDao.getContactRxMaybe(1)
            .subscribe(
                { contactInfo ->
                    assertTrue(true)
                },
                {
                    assertTrue(false)
                },
                {
                    assertTrue(false)
                },
            )
    }

    // 可能有非同步問題
    @SuppressLint("CheckResult")
    @Test
    fun getContactRxMaybe_should_failed01() {

        contactInfoDao.getContactRxMaybe(100)
            .subscribe(
                { contactInfo ->
                    assertTrue(false)
                },
                {
                    assertTrue(false)
                },
                {
                    assertTrue(true)
                },
            )
    }
}