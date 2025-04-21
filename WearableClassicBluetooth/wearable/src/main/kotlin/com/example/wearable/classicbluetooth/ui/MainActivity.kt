package com.example.wearable.classicbluetooth.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.example.wearable.classicbluetooth.service.BluetoothService
import com.example.wearable.classicbluetooth.ui.viewmodel.MainViewModel
import com.example.wearable.classicbluetooth.ui.viewmodel.MainViewModelFactory
import com.example.wearable.classicbluetooth.util.hasAllPermissions
import com.example.wearable.classicbluetooth.util.requiredPermissions

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels { MainViewModelFactory(this) }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        requestPermission()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermission()
    }

    /**
     * Request Permission
     */
    private fun requestPermission() {

        if (hasAllPermissions()) {

            initScreen()
            startBluetoothService()

        } else {

            requestPermissionLauncher.launch(requiredPermissions)
        }
    }

    /**
     * Initialize Screen
     */
    private fun initScreen() {

        setContent {
            MainScreen(viewModel)
        }
    }

    /**
     * Start Bluetooth Service
     */
    private fun startBluetoothService() {

        val serviceIntent = Intent(this, BluetoothService::class.java)
        startForegroundService(serviceIntent)
    }
}