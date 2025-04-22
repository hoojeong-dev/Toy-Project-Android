package com.example.wearable.classicbluetooth.ui

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
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
    private var bluetoothService: BluetoothService? = null
    private var isBound = false

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        requestPermission()
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {

            val binder = service as BluetoothService.LocalBinder
            bluetoothService = binder.getService()
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {

            isBound = false
            bluetoothService = null

            // 서비스 재연결 시도
            bindBluetoothService()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermission()
    }

    override fun onStart() {
        super.onStart()

        if (hasAllPermissions()) {
            bindBluetoothService()
        }
    }

    override fun onStop() {
        super.onStop()

        unbindBluetoothService()
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

        try {

            val serviceIntent = Intent(this, BluetoothService::class.java)
            startForegroundService(serviceIntent)

        } catch (e: Exception) {
            Log.d("BluetoothService", "startBluetoothService: $e")
        }
    }

    private fun bindBluetoothService() {

        val intent = Intent(this, BluetoothService::class.java)
        bindService(intent, serviceConnection, BIND_AUTO_CREATE)
    }

    private fun unbindBluetoothService() {

        if (isBound) {

            unbindService(serviceConnection)
            isBound = false
        }
    }
}