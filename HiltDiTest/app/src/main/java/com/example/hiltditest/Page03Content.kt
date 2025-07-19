package com.example.hiltditest

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

// 測試 DI 搭配 Repository Pattern 架構，並驗證注入 Singleton 的物件 (hash code)

// <editor-fold desc="--- View ---">

@Composable
fun Page03Screen(
    userName: String,
    productName: String,
    onFetchData: () -> Unit,
    onLogHash: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val textFontSize = 24.sp

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "User: $userName",
                fontSize = textFontSize
            )
            Text(
                text = "Product: $productName",
                fontSize = textFontSize
            )
            Button(
                onClick = { onFetchData() }
            ) {
                Text(text = "Fetch Data")
            }
            Button(
                onClick = { onLogHash() }
            ) {
                Text(text = "Log Hash")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Page03ScreenPreview() {
    Page03Screen(
        userName = "Peter",
        productName = "Water Ball",
        onFetchData = {},
        onLogHash = {}
    )
}

// </editor-fold>

// <editor-fold desc="--- ViewModel ---">

@HiltViewModel
class Page03ViewModel @Inject constructor(
    private val userService: UserService,
    private val productService: ProductService
) : ViewModel() {

    private val _user = MutableStateFlow("")
    val user = _user.asStateFlow()

    private val _product = MutableStateFlow("")
    val product = _product.asStateFlow()

    fun fetchData() {
        _user.value = userService.fetchUserInfo()
        _product.value = productService.fetchProductInfo()
    }

    fun logHashes() {
        userService.logRepoHash()
        productService.logRepoHash()
    }
}

// </editor-fold>

// <editor-fold desc="--- DI Setting ---">

@Module
@InstallIn(SingletonComponent::class)
abstract class Page03Module {

    // Service
    @Binds
    abstract fun bindUserService(
        impl: UserServiceImpl
    ): UserService

    @Binds
    abstract fun bindProductService(
        impl: ProductServiceImpl
    ): ProductService

    // Repo
    @Binds
    abstract fun bindUserRepository(
        impl: RealRepositoryImpl
    ): UserRepository

    @Binds
    abstract fun bindProductRepository(
        impl: RealRepositoryImpl
    ): ProductRepository
}

// </editor-fold>

// <editor-fold desc="--- Service ---">

interface UserService {
    fun fetchUserInfo(): String
    fun logRepoHash()
}

interface ProductService {
    fun fetchProductInfo(): String
    fun logRepoHash()
}

class UserServiceImpl @Inject constructor(
    private val userRepo: UserRepository
) : UserService {
    override fun fetchUserInfo(): String = userRepo.getUserName()

    override fun logRepoHash() {
        Log.i("TestRun", "UserRepository hash: ${userRepo.hashCode()}")
    }
}

class ProductServiceImpl @Inject constructor(
    private val productRepo: ProductRepository
) : ProductService {
    override fun fetchProductInfo(): String = productRepo.getProductName()

    override fun logRepoHash() {
        Log.i("TestRun", "ProductRepository hash: ${productRepo.hashCode()}")
    }
}

// </editor-fold>

// <editor-fold desc="--- Repo ---">

interface UserRepository {
    fun getUserName(): String
}

interface ProductRepository {
    fun getProductName(): String
}

// 若未加 @Singleton 此範例 DI 會 new 兩實體
@Singleton
class RealRepositoryImpl @Inject constructor() : UserRepository, ProductRepository {

    private val userNames = listOf("Alice", "Bob", "Charlie", "Diana")
    private val productNames = listOf("Coffee Mug", "Laptop", "Notebook", "Water Bottle")

    override fun getUserName(): String {
        return userNames.random()
    }

    override fun getProductName(): String {
        return productNames.random()
    }
}

// </editor-fold>