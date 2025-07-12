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
import androidx.lifecycle.viewModelScope
import com.example.roomsqlitetest.room.User
import com.example.roomsqlitetest.room.UserDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// <editor-fold desc="--- View ---">

//@Composable
//fun Page01Entry(page01Vm: Page01ViewModel) {
//    val page01name by page01Vm.name.collectAsStateWithLifecycle()
//    val page01age by page01Vm.age.collectAsStateWithLifecycle()
//    val page01birthday by page01Vm.birthday.collectAsStateWithLifecycle()
//
//    val formState = remember(page01Vm, page01name, page01age, page01birthday) {
//        UserFormState(
//            name = page01name,
//            age = page01age,
//            birthday = page01birthday,
//            onNameChange = page01Vm::setName,
//            onAgeChange = page01Vm::setAge,
//            onBirthdayChange = page01Vm::setBirthday,
//            onSubmit = page01Vm::insertUser,
//            onQueryAll = page01Vm::logAllUsersBlocking,
//            onQueryOverAge = { page01Vm.logUsersOverBlocking(it) },
//            onQuerySuspend = { page01Vm.logUsersOverSuspend(it) }
//        )
//    }
//
//    Page01Screen(formState = formState)
//}

@Composable
fun Page01Screen(
    name: String,
    age: String,
    birthday: String,
    onNameChange: (String) -> Unit,
    onAgeChange: (String) -> Unit,
    onBirthdayChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onQueryAll: () -> Unit,
    onQueryOverAge: (Int) -> Unit,
    onQuerySuspend: (Int) -> Unit,
    onDeleteFirstUser: () -> Unit
) {
    var minAge by rememberSaveable { mutableStateOf("25") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text("User Input", style = MaterialTheme.typography.titleMedium)

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
        Text("Query Controls", style = MaterialTheme.typography.titleMedium)

        Spacer(Modifier.height(8.dp))
        TextField(
            value = minAge,
            onValueChange = { minAge = it },
            label = { Text("Min Age for Filtering") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))
        Button(onClick = onQueryAll) {
            Text("Query All Users")
        }

        Spacer(Modifier.height(8.dp))
        Button(onClick = {
            val value = minAge.toIntOrNull() ?: 0
            onQueryOverAge(value)
        }) {
            Text("Query Age > Min Age (Blocking)")
        }

        Spacer(Modifier.height(8.dp))
        Button(onClick = {
            val value = minAge.toIntOrNull() ?: 0
            onQuerySuspend(value)
        }) {
            Text("Query Age > Min Age (Suspend)")
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
fun Page01ScreenPreview() {
    Page01Screen(
        name = "Peter",
        age = "26",
        birthday = "2000.05.25",
        onNameChange = {},
        onAgeChange = {},
        onBirthdayChange = {},
        onSubmit = {},
        onQueryAll = {},
        onQueryOverAge = {},
        onQuerySuspend = {},
        onDeleteFirstUser = {}
    )
}

// </editor-fold>

// <editor-fold desc="--- ViewModel ---">

@HiltViewModel
class Page01ViewModel @Inject constructor(
    private val userDao: UserDao
) : ViewModel() {

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
    fun insertUser() {
        viewModelScope.launch(Dispatchers.IO) {
            val user = User(
                name = _name.value,
                age = _age.value.toIntOrNull() ?: 0,
                birthday = _birthday.value
            )
            userDao.insert(user)
            Log.i("Page01", "Inserted: $user")
        }
    }

    // 使用同步查詢(避免主線程，需 Dispatchers.IO)
    fun logAllUsersBlocking() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = userDao.getAllUserBlocking()
            Log.i("Page01", "All users: $result")
        }
    }

    // 查詢年齡大於指定值的使用者
    fun logUsersOverBlocking(minAge: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = userDao.getUsersOver(minAge)
            Log.i("Page01", "Users over $minAge: $result")
        }
    }

    // 使用 suspend 版本查詢
    fun logUsersOverSuspend(minAge: Int) {
        viewModelScope.launch {
            val result = userDao.getUsersOverSuspend(minAge)
            Log.i("Page01", "Suspend result over $minAge: $result")
        }
    }

    // 使用 delete 示範
    // 實務上是可以在 dao 來直接達成的
    fun deleteFirstUser() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = userDao.getAllUserBlocking()
            if (result.isNotEmpty()) {
                val firstUser = result.first()
                userDao.delete(firstUser)
                Log.i("Page01", "Deleted user: $firstUser")
            } else {
                Log.i("Page01", "No user to delete")
            }
        }
    }
}

// </editor-fold>