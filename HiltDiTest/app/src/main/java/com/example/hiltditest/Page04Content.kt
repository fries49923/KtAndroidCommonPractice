package com.example.hiltditest

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

// 測試直接從 Compose 中取得 ViewModel
// 配合此範例的 ModalNavigationDrawer 在切換頁面時，並不會觸發到 viewModel 的 onCleared()
// 不過會造成 MainScreenPreview 切到 page04 時會卡住

// <editor-fold desc="--- View ---">

@Composable
fun Page04Page(
    page04Vm: Page04ViewModel = hiltViewModel<Page04ViewModel>()
) {
    val msg = page04Vm.message.collectAsStateWithLifecycle()

    Page04Screen(
        msg = msg.value, onFresh = { page04Vm.freshMessage() })
}

@Composable
fun Page04Screen(
    msg: String, onFresh: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Msg: $msg",
                fontSize = 24.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { onFresh() }) {
                Text(text = "Fresh")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Page04ScreenPreview() {
    Page04Screen(
        msg = "This is message", onFresh = {})
}

// </editor-fold>

// <editor-fold desc="--- ViewModel ---">

@HiltViewModel
class Page04ViewModel @Inject constructor(
    private val service: MessageProvider
) : ViewModel() {

    private val _message = MutableStateFlow("Loading...")
    val message: StateFlow<String> = _message.asStateFlow()

    init {
        Log.i("TestRun", "Page04ViewModel init, hashCode=${this.hashCode()}")
        Log.i("TestRun", "Injected MessageProvider hashCode=${service.hashCode()}")
        freshMessage()
    }

    fun freshMessage() {
        _message.value = service.fetchMessage()
    }

    override fun onCleared() {
        Log.i("TestRun", "Page04ViewModel onCleared")
        super.onCleared()
    }
}

// </editor-fold>

// <editor-fold desc="--- DI Setting ---">

@Module
@InstallIn(SingletonComponent::class)
abstract class Page04Module {

    @Binds
    abstract fun bindPage04Service(
        impl: MessageProviderImpl
    ): MessageProvider
}

// </editor-fold>

// <editor-fold desc="--- Service ---">

interface MessageProvider {
    fun fetchMessage(): String
}

//@Singleton
class MessageProviderImpl @Inject constructor() : MessageProvider {
    private val messages = listOf(
        "Hello from Page04!", "Welcome to Page04!", "This is a message from Page04."
    )

    override fun fetchMessage(): String {
        return messages.random()
    }
}

// </editor-fold>