package com.example.roomsqlitetest

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
import com.example.roomsqlitetest.ui.theme.RoomSqliteTestTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val page01Vm: Page01ViewModel by viewModels()
    private val page02Vm: Page02ViewModel by viewModels()
    private val page03Vm: Page03ViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RoomSqliteTestTheme {
                MainScreenPage(
                    page01Vm,
                    page02Vm,
                    page03Vm
                )
            }
        }
    }
}

@Composable
fun MainScreenPage(
    page01Vm: Page01ViewModel,
    page02Vm: Page02ViewModel,
    page03Vm: Page03ViewModel
) {
    val page01Name by page01Vm.name.collectAsStateWithLifecycle()
    val page01Age by page01Vm.age.collectAsStateWithLifecycle()
    val page01Birthday by page01Vm.birthday.collectAsStateWithLifecycle()

    val page02Name by page02Vm.name.collectAsStateWithLifecycle()
    val page02Age by page02Vm.age.collectAsStateWithLifecycle()
    val page02Birthday by page02Vm.birthday.collectAsStateWithLifecycle()

    MainScreen(
        page01Name = page01Name,
        page01Age = page01Age,
        page01Birthday = page01Birthday,
        page01OnNameChange = page01Vm::setName, // :: 或下面用 it 皆可
        page01OnAgeChange = { page01Vm.setAge(it) },
        page01OnBirthdayChange = page01Vm::setBirthday,
        page01OnSubmit = page01Vm::insertUser,
        page01OnQueryAll = page01Vm::logAllUsersBlocking,
        page01OnQueryOverAge = page01Vm::logUsersOverBlocking,
        page01OnQuerySuspend = page01Vm::logUsersOverSuspend,
        page01OnDeleteFirstUser = page01Vm::deleteFirstUser,
        page02Name = page02Name,
        page02Age = page02Age,
        page02Birthday = page02Birthday,
        page02OnNameChange = page02Vm::setName,
        page02OnAgeChange = page02Vm::setAge,
        page02OnBirthdayChange = page02Vm::setBirthday,
        page02OnSubmit = page02Vm::insertUserRx,
        page02OnQueryAllOnce = page02Vm::logAllUsersRxOnce,
        page02OnQueryAllStream = page02Vm::logAllUsersRxStream,
        page02OnQueryOverAgeSingle = page02Vm::logUsersOverRxSingle,
        page02OnQueryOverAgeMaybe = page02Vm::logUsersOverRxMaybe,
        page02OnDeleteFirstUser = page02Vm::deleteFirstUserRx,
        page03OnInsertInfo = page03Vm::insertContactInfoRx,
        page03OnQueryInfoSingle = page03Vm::getContactInfoRxSingle,
        page03OnQueryInfoMaybe = page03Vm::getContactInfoRxMaybe
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    page01Name: String,
    page01Age: String,
    page01Birthday: String,
    page01OnNameChange: (String) -> Unit,
    page01OnAgeChange: (String) -> Unit,
    page01OnBirthdayChange: (String) -> Unit,
    page01OnSubmit: () -> Unit,
    page01OnQueryAll: () -> Unit,
    page01OnQueryOverAge: (Int) -> Unit,
    page01OnQuerySuspend: (Int) -> Unit,
    page01OnDeleteFirstUser: () -> Unit,
    page02Name: String,
    page02Age: String,
    page02Birthday: String,
    page02OnNameChange: (String) -> Unit,
    page02OnAgeChange: (String) -> Unit,
    page02OnBirthdayChange: (String) -> Unit,
    page02OnSubmit: () -> Unit,
    page02OnQueryAllOnce: () -> Unit,
    page02OnQueryAllStream: () -> Unit,
    page02OnQueryOverAgeSingle: (Int) -> Unit,
    page02OnQueryOverAgeMaybe: (Int) -> Unit,
    page02OnDeleteFirstUser: () -> Unit,
    page03OnInsertInfo: (Int, String, String) -> Unit,
    page03OnQueryInfoSingle: (Int) -> Unit,
    page03OnQueryInfoMaybe: (Int) -> Unit
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
                page01Name = page01Name,
                page01Age = page01Age,
                page01Birthday = page01Birthday,
                page01OnNameChange = page01OnNameChange,
                page01OnAgeChange = page01OnAgeChange,
                page01OnBirthdayChange = page01OnBirthdayChange,
                page01OnSubmit = page01OnSubmit,
                page01OnQueryAll = page01OnQueryAll,
                page01OnQueryOverAge = page01OnQueryOverAge,
                page01OnQuerySuspend = page01OnQuerySuspend,
                page01OnDeleteFirstUser = page01OnDeleteFirstUser,
                page02Name = page02Name,
                page02Age = page02Age,
                page02Birthday = page02Birthday,
                page02OnNameChange = page02OnNameChange,
                page02OnAgeChange = page02OnAgeChange,
                page02OnBirthdayChange = page02OnBirthdayChange,
                page02OnSubmit = page02OnSubmit,
                page02OnQueryAllOnce = page02OnQueryAllOnce,
                page02OnQueryAllStream = page02OnQueryAllStream,
                page02OnQueryOverAgeSingle = page02OnQueryOverAgeSingle,
                page02OnQueryOverAgeMaybe = page02OnQueryOverAgeMaybe,
                page02OnDeleteFirstUser = page02OnDeleteFirstUser,
                page03OnInsertInfo = page03OnInsertInfo,
                page03OnQueryInfoSingle = page03OnQueryInfoSingle,
                page03OnQueryInfoMaybe = page03OnQueryInfoMaybe,
                modifier = Modifier.padding(padding)
            )
        }
    }
}

@Composable
fun ScreenContainer(
    selectedPage: Int,
    page01Name: String,
    page01Age: String,
    page01Birthday: String,
    page01OnNameChange: (String) -> Unit,
    page01OnAgeChange: (String) -> Unit,
    page01OnBirthdayChange: (String) -> Unit,
    page01OnSubmit: () -> Unit,
    page01OnQueryAll: () -> Unit,
    page01OnQueryOverAge: (Int) -> Unit,
    page01OnQuerySuspend: (Int) -> Unit,
    page01OnDeleteFirstUser: () -> Unit,
    page02Name: String,
    page02Age: String,
    page02Birthday: String,
    page02OnNameChange: (String) -> Unit,
    page02OnAgeChange: (String) -> Unit,
    page02OnBirthdayChange: (String) -> Unit,
    page02OnSubmit: () -> Unit,
    page02OnQueryAllOnce: () -> Unit,
    page02OnQueryAllStream: () -> Unit,
    page02OnQueryOverAgeSingle: (Int) -> Unit,
    page02OnQueryOverAgeMaybe: (Int) -> Unit,
    page02OnDeleteFirstUser: () -> Unit,
    page03OnInsertInfo: (Int, String, String) -> Unit,
    page03OnQueryInfoSingle: (Int) -> Unit,
    page03OnQueryInfoMaybe: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        when (selectedPage) {
            0 -> Page01Screen(
                name = page01Name,
                age = page01Age,
                birthday = page01Birthday,
                onNameChange = page01OnNameChange,
                onAgeChange = page01OnAgeChange,
                onBirthdayChange = page01OnBirthdayChange,
                onSubmit = page01OnSubmit,
                onQueryAll = page01OnQueryAll,
                onQueryOverAge = page01OnQueryOverAge,
                onQuerySuspend = page01OnQuerySuspend,
                onDeleteFirstUser = page01OnDeleteFirstUser
            )

            1 -> Page02Screen(
                name = page02Name,
                age = page02Age,
                birthday = page02Birthday,
                onNameChange = page02OnNameChange,
                onAgeChange = page02OnAgeChange,
                onBirthdayChange = page02OnBirthdayChange,
                onSubmit = page02OnSubmit,
                onQueryAllOnce = page02OnQueryAllOnce,
                onQueryAllStream = page02OnQueryAllStream,
                onQueryOverAgeSingle = page02OnQueryOverAgeSingle,
                onQueryOverAgeMaybe = page02OnQueryOverAgeMaybe,
                onDeleteFirstUser = page02OnDeleteFirstUser
            )

            2 -> Page03Screen(
                onInsertInfo = page03OnInsertInfo,
                onQueryInfoSingle = page03OnQueryInfoSingle,
                onQueryInfoMaybe = page03OnQueryInfoMaybe
            )
        }
    }
}

@Preview
@Composable
fun MainScreenPreview() {
    MainScreen(
        page01Name = "Peter",
        page01Age = "26",
        page01Birthday = "2000.05.25",
        page01OnNameChange = {},
        page01OnAgeChange = {},
        page01OnBirthdayChange = {},
        page01OnSubmit = {},
        page01OnQueryAll = {},
        page01OnQueryOverAge = {},
        page01OnQuerySuspend = {},
        page01OnDeleteFirstUser = {},
        page02Name = "Peter",
        page02Age = "26",
        page02Birthday = "2000.05.25",
        page02OnNameChange = {},
        page02OnAgeChange = {},
        page02OnBirthdayChange = {},
        page02OnSubmit = {},
        page02OnQueryAllOnce = {},
        page02OnQueryAllStream = {},
        page02OnQueryOverAgeSingle = {},
        page02OnQueryOverAgeMaybe = {},
        page02OnDeleteFirstUser = {},
        page03OnInsertInfo = { _, _, _ -> },
        page03OnQueryInfoSingle = {},
        page03OnQueryInfoMaybe = {}
    )
}