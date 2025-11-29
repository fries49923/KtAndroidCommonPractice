package com.example.productflavortest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.productflavortest.ui.theme.ProductFlavorTestTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProductFlavorTestTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = 80.dp,
                start = 20.dp,
                end = 20.dp
            ),
    ) {
        val fontSize = 24.sp

        // 讀取 AndroidManifest.xml 中的 android:label 對應字串資源 (app_name)
        // 不同 Flavor 可以透過 src/<flavor>/res/values/strings.xml 覆蓋 app_name
        val appName = "app name:\n" + stringResource(R.string.app_name)
        Text(
            fontSize = fontSize,
            text = appName
        )
        Spacer(modifier = Modifier.height(20.dp))

        // 讀取 build.gradle.kts 中 productFlavors 設定的 buildConfigField
        // 在 BuildConfig 類別裡注入 API_URL，依照 dev/prod Flavor 會有不同值
        val flavorUrl = "url:\n" + BuildConfig.API_URL
        Text(
            fontSize = fontSize,
            text = flavorUrl
        )
        Spacer(modifier = Modifier.height(20.dp))

        // 讀取 BuildConfig.VERSION_NAME，會包含 versionNameSuffix (例如 1.0.0-dev)
        val versionName = "version: " + BuildConfig.VERSION_NAME
        Text(
            fontSize = fontSize,
            text = versionName
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    ProductFlavorTestTheme {
        MainScreen()
    }
}