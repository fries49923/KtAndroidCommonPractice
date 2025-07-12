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
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

// 測試透過介面與模組注入不同邏輯的實作到 ViewModel 中

@Composable
fun Page02Screen(
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
fun Page02ScreenPreview() {
    Page02Screen(
        count = 10,
        onIncrement = {}
    )
}

@HiltViewModel
class Page02ViewModel @Inject constructor(
    private val processor: IntProcessor
) : ViewModel() {

    private val _data = MutableStateFlow(0)
    val data: StateFlow<Int> = _data.asStateFlow()

    fun increment() {
        _data.value = processor.process(_data.value)
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class ProcessorModule {
    @Binds
    abstract fun bindIntProcessor(
        //impl: AddOneProcessor
        impl: AddTwoProcessor
    ): IntProcessor
}

interface IntProcessor {
    fun process(value: Int): Int
}

@Singleton
class AddOneProcessor @Inject constructor() : IntProcessor {
    override fun process(value: Int): Int = value + 1
}

@Singleton
class AddTwoProcessor @Inject constructor() : IntProcessor {
    override fun process(value: Int): Int = value + 2
}