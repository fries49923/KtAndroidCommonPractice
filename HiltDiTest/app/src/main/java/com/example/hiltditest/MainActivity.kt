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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
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
    private val page02Vm: Page02ViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HiltDiTestTheme {
                MainScreenPage(
                    page01Vm,
                    page02Vm
                )
            }
        }
    }
}

@Composable
fun MainScreenPage(
    page01Vm: Page01ViewModel,
    page02Vm: Page02ViewModel
) {
    val page01Count by page01Vm.data.collectAsStateWithLifecycle()

    val page02User by page02Vm.user.collectAsStateWithLifecycle()
    val page02Product by page02Vm.product.collectAsStateWithLifecycle()

    MainScreen(
        page01Count = page01Count,
        page01OnIncrement = { page01Vm.increment() },
        page02UserName = page02User,
        page02ProName = page02Product,
        page02OnFetchData = { page02Vm.fetchData() },
        page02OnLogHash = { page02Vm.logHashes() },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    page01Count: Int,
    page01OnIncrement: () -> Unit,
    page02UserName: String,
    page02ProName: String,
    page02OnFetchData: () -> Unit,
    page02OnLogHash: () -> Unit,
) {
    val drawerItems = listOf("Page01", "Page02", "Page03")

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedPage by rememberSaveable { mutableIntStateOf(0) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {

                // 往下推一點空間
                Spacer(modifier = Modifier.height(100.dp))

                drawerItems.forEachIndexed { index, label ->
                    NavigationDrawerItem(
                        label = { Text(label) },
                        selected = selectedPage == index,
                        onClick = {
                            selectedPage = index
                            scope.launch { drawerState.close() }
                        }
                    )
                }
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
                selectedPage = selectedPage,
                page01Count = page01Count,
                page01OnIncrement = page01OnIncrement,
                page02UserName = page02UserName,
                page02ProName = page02ProName,
                page02OnFetchData = page02OnFetchData,
                page02OnLogHash = page02OnLogHash,
                modifier = Modifier.padding(padding)
            )
        }
    }
}

@Composable
fun ScreenContainer(
    selectedPage: Int,
    page01Count: Int,
    page01OnIncrement: () -> Unit,
    page02UserName: String,
    page02ProName: String,
    page02OnFetchData: () -> Unit,
    page02OnLogHash: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        when (selectedPage) {
            0 -> Page01Screen(
                count = page01Count,
                onIncrement = page01OnIncrement
            )

            1 -> Page02Screen(
                userName = page02UserName,
                productName = page02ProName,
                onFetchData = page02OnFetchData,
                onLogHash = page02OnLogHash
            )

            2 -> Page03Page()
        }
    }
}

@Preview
@Composable
fun MainScreenPreview() {
    MainScreen(
        page01Count = 10,
        page01OnIncrement = {},
        page02UserName = "Peter",
        page02ProName = "Water Ball",
        page02OnFetchData = {},
        page02OnLogHash = {}
    )
}