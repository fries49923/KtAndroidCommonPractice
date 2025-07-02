package com.example.hiltditest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.hiltditest.ui.theme.HiltDiTestTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    page01Count: Int,
    page01OnIncrement: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedPage by remember { mutableIntStateOf(0) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {

                // 往下推一點空間
                Spacer(modifier = Modifier.height(100.dp))

                // Page01
                NavigationDrawerItem(
                    label = { Text("Page01") },
                    selected = selectedPage == 0,
                    onClick = {
                        selectedPage = 0
                        scope.launch { drawerState.close() }
                    }
                )

                // Page02
                NavigationDrawerItem(
                    label = { Text("Page02") },
                    selected = selectedPage == 1,
                    onClick = {
                        selectedPage = 1
                        scope.launch { drawerState.close() }
                    }
                )
                // 擴充更多項目
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("App title") },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                scope.launch { drawerState.open() }
                            }) {
                            Icon(
                                Icons.Default.Menu,
                                contentDescription = null
                            )
                        }
                    }
                )
            }
        ) { padding ->
            ScreenContainer(
                page01Count = page01Count,
                page01OnIncrement = page01OnIncrement,
                selectedPage = selectedPage,
                modifier = Modifier.padding(padding)
            )
        }
    }
}

@Composable
fun ScreenContainer(
    page01Count: Int,
    page01OnIncrement: () -> Unit,
    selectedPage: Int,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        when (selectedPage) {
            0 -> Page01Screen(
                count = page01Count,
                onIncrement = page01OnIncrement
            )
            //1 -> Page2Screen()
        }
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