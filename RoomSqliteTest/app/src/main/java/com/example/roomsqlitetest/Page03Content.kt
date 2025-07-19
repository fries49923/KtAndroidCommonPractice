package com.example.roomsqlitetest

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.example.roomsqlitetest.room.ContactInfo
import com.example.roomsqlitetest.room.ContactInfoDao
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

// <editor-fold desc="--- View ---">

@Composable
fun Page03Screen(
    onInsertInfo: (Int, String, String) -> Unit,
    onQueryInfoSingle: (Int) -> Unit,
    onQueryInfoMaybe: (Int) -> Unit
) {
    var id by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }

    var queryId by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "Contact Info Input", style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.height(8.dp))
        TextField(
            value = id,
            onValueChange = { id = it },
            label = { Text("User Id") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))
        TextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                onInsertInfo(
                    id.toIntOrNull() ?: 0, email, phone
                )
            }) {
            Text("Insert Contact Info")
        }

        Spacer(Modifier.height(24.dp))
        Text(
            "Query Contact Info", style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.height(8.dp))
        TextField(
            value = queryId,
            onValueChange = { queryId = it },
            label = { Text("User Id") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                onQueryInfoSingle(
                    queryId.toIntOrNull() ?: 0
                )
            }) {
            Text("Query Contact Info Single")
        }

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                onQueryInfoMaybe(
                    queryId.toIntOrNull() ?: 0
                )
            }) {
            Text("Query Contact Info Maybe")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Page03ScreenPreview() {
    Page03Screen(
        onInsertInfo = { _, _, _ -> },
        onQueryInfoSingle = {},
        onQueryInfoMaybe = {}
    )
}

// </editor-fold>

// <editor-fold desc="--- ViewModel ---">

@HiltViewModel
class Page03ViewModel @Inject constructor(
    private val contactInfoDao: ContactInfoDao
) : ViewModel() {

    private val disposable = CompositeDisposable()

    fun insertContactInfoRx(
        id: Int, email: String, phone: String
    ) {
        val info = ContactInfo(
            userId = id, email = email, phone = phone
        )

        val insertDisposable =
            contactInfoDao.insertRx(info)
                .subscribeOn(Schedulers.io())
                .subscribe({
                    Log.i("Page03", "Inserted: $info")
                }, {
                    Log.e("Page03", "Insert error", it)
                })

        disposable.add(insertDisposable)
    }

    fun getContactInfoRxSingle(id: Int) {
        val queryDisposable = contactInfoDao.getContactRxSingle(id)
            .subscribeOn(Schedulers.io())
            .subscribe({ result ->
            Log.i("Page03", "Rx Single contactInfo: $result")
        }, {
            Log.e("Page03", "Rx Single error", it)
        })

        disposable.add(queryDisposable)
    }

    fun getContactInfoRxMaybe(id: Int) {
        val queryDisposable = contactInfoDao.getContactRxMaybe(id)
            .subscribeOn(Schedulers.io())
            .subscribe({ result ->
                Log.i("Page03", "Rx Maybe contactInfo: $result")
            }, {
                Log.e("Page03", "Rx Maybe error", it)
            },{
                Log.i("Page03", "Rx Maybe completed with no user $id")
            })

        disposable.add(queryDisposable)
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}

// </editor-fold>