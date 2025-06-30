package com.example.hiltditest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.hiltditest.ui.theme.HiltDiTestTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val page01Vm: Page01ViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HiltDiTestTheme {
                MainScreenPage(page01Vm)
            }
        }
    }
}

@Composable
fun MainScreenPage(
    page01Vm: Page01ViewModel
) {
    val page01Count by page01Vm.data.collectAsStateWithLifecycle()

    MainScreen(
        page01Count = page01Count,
        page01OnIncrement = { page01Vm.increment() }
    )
}

@Composable
fun MainScreen(
    page01Count: Int,
    page01OnIncrement: () -> Unit
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedTabIndex = selectedTabIndex,
                onTabSelected = { selectedTabIndex = it }
            )
        }
    ) { padding ->
        ScreenContainer(
            page01Count = page01Count,
            page01OnIncrement = page01OnIncrement,
            selectedTabIndex = selectedTabIndex,
            Modifier.padding(padding)
        )
    }
}

@Composable
fun ScreenContainer(
    page01Count: Int,
    page01OnIncrement: () -> Unit,
    selectedTabIndex: Int,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        when (selectedTabIndex) {
            0 -> Page01Screen(
                count = page01Count,
                onIncrement = page01OnIncrement
            )
            //1 -> Page2Screen()
        }
    }
}

@Composable
private fun BottomNavigationBar(
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
    ) {
        // Page01
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null
                )
            },
            label = {
                Text("Page01")
            },
            selected = selectedTabIndex == 0,
            onClick = { onTabSelected(0) },
        )

        // Page02
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null
                )
            },
            label = {
                Text("Page02")
            },
            selected = selectedTabIndex == 1,
            onClick = { onTabSelected(1) },
        )
    }
}

@Preview
@Composable
fun MainScreenPreview() {
    MainScreen(
        page01Count = 10,
        page01OnIncrement = {}
    )
}