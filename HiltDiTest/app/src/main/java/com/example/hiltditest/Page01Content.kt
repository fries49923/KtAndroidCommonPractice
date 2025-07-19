package com.example.hiltditest

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

// 測試基礎 DI，注入固定物件到 ViewModel 中

@Composable
fun Page01Screen(
    count: Int,
    onIncrement: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Count: $count", fontSize = 24.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { onIncrement() }
            ) {
                Text(text = "Increase")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Page01ScreenPreview() {
    Page01Screen(
        count = 10,
        onIncrement = {}
    )
}

@HiltViewModel
class Page01ViewModel @Inject constructor(
    private val processor: SimpleProcessor
) : ViewModel() {

    private val _data = MutableStateFlow(0)
    val data: StateFlow<Int> = _data.asStateFlow()

    fun increment() {
        _data.value = processor.process(_data.value)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object SimpleProcessorModule {

    @Provides
    @Singleton
    fun provideSimpleProcessor(): SimpleProcessor = SimpleProcessor()
}

@Singleton
class SimpleProcessor @Inject constructor() {
    fun process(value: Int): Int = value + 10
}