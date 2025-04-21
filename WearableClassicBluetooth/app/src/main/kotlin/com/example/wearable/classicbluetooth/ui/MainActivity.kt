package com.example.wearable.classicbluetooth.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.wearable.classicbluetooth.util.REQUEST_CODE_BLUETOOTH
import com.example.wearable.classicbluetooth.util.hasBluetoothConnectPermission
import com.example.wearable.classicbluetooth.util.requestBluetoothPermissions
import com.example.wearable.classicbluetooth.ui.theme.WearableClassicBluetoothTheme
import com.example.wearable.classicbluetooth.ui.viewmodel.MainViewModel
import com.example.wearable.classicbluetooth.ui.viewmodel.MainViewModelFactory

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels { MainViewModelFactory(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermission()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String?>, grantResults: IntArray, deviceId: Int) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)

        if (requestCode == REQUEST_CODE_BLUETOOTH) {
            requestPermission()
        }
    }

    private fun requestPermission() {
        if (!hasBluetoothConnectPermission()) {
            requestBluetoothPermissions(REQUEST_CODE_BLUETOOTH)
        } else {
            initScreen()
        }
    }

    private fun initScreen() {
        setContent {
            WearableClassicBluetoothTheme {
                MainScreen(viewModel)
            }
        }
    }
}