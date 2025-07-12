package com.example.roomsqlitetest

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.example.roomsqlitetest.room.User
import com.example.roomsqlitetest.room.UserDao
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

// <editor-fold desc="--- View ---">

@Composable
fun Page02Screen(
    name: String,
    age: String,
    birthday: String,
    onNameChange: (String) -> Unit,
    onAgeChange: (String) -> Unit,
    onBirthdayChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onQueryAllOnce: () -> Unit,
    onQueryAllStream: () -> Unit,
    onQueryOverAgeSingle: (Int) -> Unit,
    onQueryOverAgeMaybe: (Int) -> Unit,
    onDeleteFirstUser: () -> Unit
) {
    var minAge by rememberSaveable { mutableStateOf("25") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            "User Input",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.height(8.dp))
        TextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))
        TextField(
            value = age,
            onValueChange = onAgeChange,
            label = { Text("Age") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))
        TextField(
            value = birthday,
            onValueChange = onBirthdayChange,
            label = { Text("Birthday") },
            placeholder = { Text("e.g. 1998.06.20") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))
        Button(onClick = onSubmit) {
            Text("Insert User")
        }

        Spacer(Modifier.height(24.dp))
        Text(
            "Query Controls",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.height(8.dp))
        TextField(
            value = minAge,
            onValueChange = { minAge = it },
            label = { Text("Min Age for Filtering") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))
        Button(onClick = onQueryAllOnce) {
            Text("Query All Users Once")
        }

        Spacer(Modifier.height(8.dp))
        Button(onClick = onQueryAllStream) {
            Text("Query All Users Stream")
        }

        Spacer(Modifier.height(8.dp))
        Button(onClick = {
            val value = minAge.toIntOrNull() ?: 0
            onQueryOverAgeSingle(value)
        }) {
            Text("Query Age > Min Age (Single)")
        }

        Spacer(Modifier.height(8.dp))
        Button(onClick = {
            val value = minAge.toIntOrNull() ?: 0
            onQueryOverAgeMaybe(value)
        }) {
            Text("Query Age > Min Age (Maybe)")
        }

        Spacer(Modifier.height(8.dp))
        Button(onClick = {
            onDeleteFirstUser()
        }) {
            Text("Delete first user")
        }
    }
}


@Preview(showBackground = true)
@Composable
fun Page02ScreenPreview() {
    Page02Screen(
        name = "Peter",
        age = "26",
        birthday = "2000.05.25",
        onNameChange = {},
        onAgeChange = {},
        onBirthdayChange = {},
        onSubmit = {},
        onQueryAllOnce = {},
        onQueryAllStream = {},
        onQueryOverAgeSingle = {},
        onQueryOverAgeMaybe = {},
        onDeleteFirstUser = {}
    )
}

// </editor-fold>

// <editor-fold desc="--- ViewModel ---">

@HiltViewModel
class Page02ViewModel @Inject constructor(
    private val userDao: UserDao
) : ViewModel() {

    private val disposable = CompositeDisposable()

    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()

    private val _age = MutableStateFlow("")
    val age = _age.asStateFlow()

    private val _birthday = MutableStateFlow("")
    val birthday = _birthday.asStateFlow()

    fun setName(name: String) {
        _name.value = name
    }

    fun setAge(age: String) {
        _age.value = age
    }

    fun setBirthday(birthday: String) {
        _birthday.value = birthday
    }

    // 插入使用者資料(從 Compose 取得輸入)
    fun insertUserRx() {
        val user = User(
            name = _name.value,
            age = _age.value.toIntOrNull() ?: 0,
            birthday = _birthday.value
        )
        val insertDisposable = userDao.insertRx(user)
            .subscribeOn(Schedulers.io())
            .subscribe({
                Log.i("Page02", "Inserted: $user")
            }, {
                Log.e("Page02", "Insert error", it)
            })

        disposable.add(insertDisposable)
    }

    // 使用查詢所有使用者
    fun logAllUsersRxOnce() {
        val queryDisposable = userDao.getAllUsersOnceRx()
            .subscribeOn(Schedulers.io())
            .subscribe({ result ->
                Log.i("Page02", "Rx Single All users: $result")
            }, { error ->
                Log.e("Page02", "Query error", error)
            })

        disposable.add(queryDisposable)
    }

    // Flowable 若沒取消訂閱會持續收到
    fun logAllUsersRxStream() {
        val streamDisposable = userDao.observeAllUsersRx()
            .subscribeOn(Schedulers.io())
            .subscribe({ result ->
                Log.i("Page02", "Rx Flowable stream: $result")
            }, { error ->
                Log.e("Page02", "Stream error", error)
            })

        disposable.add(streamDisposable)
    }

    // 查詢年齡大於指定值的使用者 (Single)
    // 因為查詢結果回傳是list，所以沒資料，基本上會回傳空 list，所以基本上都是跑 success
    fun logUsersOverRxSingle(minAge: Int) {
        val queryDisposable = userDao.getUsersOverRxSingle(minAge)
            .subscribeOn(Schedulers.io())
            .subscribe({ result ->
                Log.i("Page02", "Rx Single users over $minAge: $result")
            }, { error ->
                Log.e("Page02", "Rx Single error", error)
            })

        disposable.add(queryDisposable)
    }

    // 查詢年齡大於指定值的使用者 (Maybe)
    // 因為查詢結果回傳是list，所以沒資料，基本上會回傳空 list，所以基本上都是跑 success
    fun logUsersOverRxMaybe(minAge: Int) {
        val queryDisposable = userDao.getUsersOverRxMaybe(minAge)
            .subscribeOn(Schedulers.io())
            .subscribe({ result ->
                Log.i("Page02", "Rx Maybe users over $minAge: $result")
            }, { error ->
                Log.e("Page02", "Rx Maybe error", error)
            }, {
                Log.i("Page02", "Rx Maybe completed with no users over $minAge")
            })

        disposable.add(queryDisposable)
    }

    // 使用 delete 示範
    // 實務上是可以在 dao 來直接達成的
    fun deleteFirstUserRx() {
        val deleteDisposable = userDao.getAllUsersOnceRx()
            .subscribeOn(Schedulers.io())
            .subscribe({ result ->
                result.firstOrNull()?.let { user ->
                    userDao.deleteRx(user)
                        .subscribeOn(Schedulers.io())
                        .subscribe({
                            Log.i("Page02", "Rx Deleted user: $user")
                        }, { error ->
                            Log.e("Page02", "Rx Delete error", error)
                        }).also { disposable.add(it) }
                } ?: Log.i("Page02", "No user to delete")
            }, { error ->
                Log.e("Page02", "Fetch before delete error", error)
            })

        disposable.add(deleteDisposable)
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}

// </editor-fold>