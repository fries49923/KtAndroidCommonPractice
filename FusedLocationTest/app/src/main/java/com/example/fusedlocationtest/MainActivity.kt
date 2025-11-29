package com.example.fusedlocationtest

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fusedlocationtest.ui.theme.FusedLocationTestTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FusedLocationTestTheme {
                LocationScreen()
            }
        }
    }
}

fun isLocationEnabled(context: Context): Boolean {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
}

@Composable
fun LocationScreen(viewModel: LocationViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (granted) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    if (isLocationEnabled(context)) {
                        viewModel.startLocationUpdates()
                    } else {
                        Toast.makeText(context, "請開啟定位服務", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    )

    val locationData by viewModel.locationData.collectAsStateWithLifecycle()

    // 在 Composable 啟動時請求權限
    LaunchedEffect(Unit) {
        launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    LocationPage(
        locationData = locationData
    )
}

@Composable
fun LocationPage(
    locationData: LocationData = LocationData()
) {
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

        Text(
            text = "緯度: ${locationData.latitude}",
            fontSize = fontSize
        )
        Text(
            text = "經度: ${locationData.longitude}",
            fontSize = fontSize
        )
        Text(
            text = "海拔高度: ${locationData.altitude} 公尺",
            fontSize = fontSize
        )
        Text(
            text = "精度: ${locationData.accuracy} 公尺",
            fontSize = fontSize
        )
        Text(
            text = "速度: ${locationData.speed} 公尺/秒",
            fontSize = fontSize
        )
        Text(
            text = "航向角度: ${locationData.bearing}°",
            fontSize = fontSize
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LocationScreenPreview() {
    FusedLocationTestTheme {
        LocationPage()
    }
}