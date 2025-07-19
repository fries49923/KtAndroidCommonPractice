package com.example.mmkvtest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mmkvtest.ui.theme.MmkvTestTheme
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MmkvTestTheme {
                MainScreenPage(
                    mainViewModel
                )
            }
        }
    }
}

@Composable
fun MainScreenPage(
    mainViewModel: MainViewModel
) {
    val name by mainViewModel.name.collectAsStateWithLifecycle()
    val count by mainViewModel.count.collectAsStateWithLifecycle()
    val themeMode by mainViewModel.themeMode.collectAsStateWithLifecycle()

    MainScreen(
        name = name,
        count = count,
        themeMode = themeMode,
        onSetData = mainViewModel::setData,
        onClearData = mainViewModel::clearData
    )
}

@Composable
fun MainScreen(
    name: String,
    count: Int,
    themeMode: ThemeMode,
    onSetData: (String, Int, ThemeMode) -> Unit,
    onClearData: () -> Unit
) {
    var nameInput by remember { mutableStateOf(name) }
    var countInput by remember { mutableStateOf(count.toString()) }
    var selectedMode by remember { mutableStateOf(themeMode) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = nameInput,
            onValueChange = { nameInput = it },
            label = { Text("Name") }
        )

        OutlinedTextField(
            value = countInput,
            onValueChange = { countInput = it },
            label = { Text("Count") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        ThemeModePicker(
            selected = selectedMode,
            onSelect = { selectedMode = it }
        )

        Button(onClick = {
            onSetData(nameInput, countInput.toIntOrNull() ?: -1, selectedMode)
        }) {
            Text("Save")
        }

        Button(onClick = onClearData) {
            Text("Clear")
        }
    }
}

@Composable
fun ThemeModePicker(
    selected: ThemeMode,
    onSelect: (ThemeMode) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Text("Theme: ${selected.name}")
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            ThemeMode.entries.forEach { mode ->
                DropdownMenuItem(
                    text = { Text(mode.name) },
                    onClick = {
                        onSelect(mode)
                        expanded = false
                    }
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen(
        name = "Peter",
        count = 26,
        themeMode = ThemeMode.DARK,
        onSetData = { _, _, _ -> },
        onClearData = {},
    )
}

// <editor-fold desc="--- ViewModel ---">

class MainViewModel : ViewModel() {

    private val mmkv = MMKV.defaultMMKV()
    private val keyName = "key_name"
    private val keyCount = "key_count"
    private val keyThemeMode = "key_themeMode"

    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()

    private val _count = MutableStateFlow(-1)
    val count = _count.asStateFlow()

    private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
    val themeMode = _themeMode.asStateFlow()

    init {
        // 示範 containsKey 方法，可作為條件判斷參考
        val hasName = mmkv.containsKey(keyName)
        if (hasName) {
            _name.value = mmkv.decodeString(keyName, "defaultValue") ?: "defaultValue"
        }

        _count.value = mmkv.decodeInt(keyCount, -1)

        _themeMode.value = runCatching {
            ThemeMode.valueOf(
                mmkv.decodeString(keyThemeMode, ThemeMode.SYSTEM.name) ?: ThemeMode.SYSTEM.name
            )
        }.getOrDefault(ThemeMode.SYSTEM)
    }

    fun setData(name: String, count: Int, mode: ThemeMode) {
        _name.value = name
        _count.value = count
        _themeMode.value = mode

        mmkv.encode(keyName, name)
        mmkv.encode(keyCount, count)
        mmkv.encode(keyThemeMode, mode.name)
    }

    fun clearData() {
        _name.value = ""
        _count.value = -1
        _themeMode.value = ThemeMode.SYSTEM

        mmkv.removeValueForKey(keyName)
        mmkv.removeValueForKey(keyCount)
        mmkv.removeValueForKey(keyThemeMode)

        // or use
        //mmkv.removeValuesForKeys(arrayOf(keyName, keyCount, keyThemeMode))
    }
}

// </editor-fold>