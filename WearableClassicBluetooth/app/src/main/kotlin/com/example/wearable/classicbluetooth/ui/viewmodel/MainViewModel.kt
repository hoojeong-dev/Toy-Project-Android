package com.example.wearable.classicbluetooth.ui.viewmodel

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wearable.classicbluetooth.bluetooth.BluetoothClientManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@SuppressLint("MissingPermission")
class MainViewModel(context: Context) : ViewModel() {

    private val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private val clientManager = BluetoothClientManager(context)

    private val _pairedDevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val pairedDevices: StateFlow<List<BluetoothDevice>> = _pairedDevices.asStateFlow()

    private val _selectedDevice = MutableStateFlow<BluetoothDevice?>(null)
    val selectedDevice: StateFlow<BluetoothDevice?> = _selectedDevice.asStateFlow()

    private val _messages = MutableStateFlow<List<String>>(emptyList())
    val messages: StateFlow<List<String>> = _messages.asStateFlow()

    init {
        loadPairedDevices()
    }

    private fun loadPairedDevices() {
        _pairedDevices.value = bluetoothAdapter?.bondedDevices?.toList() ?: emptyList()

        // 첫 번째 기기를 선택
        if (_pairedDevices.value.isNotEmpty()) {
            _selectedDevice.value = _pairedDevices.value.first()
        }
    }

    fun selectDevice(device: BluetoothDevice) {
        _selectedDevice.value = device
    }

    fun sendMessage(message: String) {

        if (message.isBlank()) return

        val device = _selectedDevice.value ?: return

        viewModelScope.launch {
            clientManager.connectAndSend(device, message) { success ->
                if (success) {
                    _messages.value = listOf("success -> $message") + _messages.value
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        clientManager.disconnect()
    }
} 