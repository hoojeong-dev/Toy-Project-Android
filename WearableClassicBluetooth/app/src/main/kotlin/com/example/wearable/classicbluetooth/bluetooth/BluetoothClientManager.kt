package com.example.wearable.classicbluetooth.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.util.Log
import com.example.wearable.classicbluetooth.constants.BluetoothConstants
import com.example.wearable.classicbluetooth.util.hasBluetoothConnectPermission
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.OutputStream

@SuppressLint("MissingPermission")
class BluetoothClientManager(private val context: Context) {

    private var socket: BluetoothSocket? = null
    private var outputStream: OutputStream? = null

    private val clientScope = CoroutineScope(Dispatchers.IO)
    private var isConnected = false

    private val _connectedDevice = MutableStateFlow<BluetoothDevice?>(null)
    val connectedDevice: StateFlow<BluetoothDevice?> = _connectedDevice

    fun connectAndSend(device: BluetoothDevice, message: String, onResult: (Boolean) -> Unit) {

        if (!context.hasBluetoothConnectPermission()) {
            onResult(false)
            return
        }

        clientScope.launch {

            try {

                // 소켓 문제 있을 경우 새로 연결
                if (socket == null || !isConnected) {
                    Log.d("BluetoothClient", "Creating new connection...")

                    socket = device.createRfcommSocketToServiceRecord(BluetoothConstants.MY_UUID)
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
                    socket?.connect()
                    outputStream = socket?.outputStream
                    isConnected = true
                    _connectedDevice.value = device

                    Log.d("BluetoothClient", "Connected to server: ${device.address}")
                }

                outputStream?.let { stream ->
                    stream.write(message.toByteArray())
                    stream.write('\n'.toInt())
                    stream.flush()
                    Log.d("BluetoothClient", "Message sent: $message")
                }

                delay(3000)

                onResult(true)

            } catch (e: IOException) {
                Log.e("BluetoothClient", "Connect error: ${e.message}")
                isConnected = false
                _connectedDevice.value = null
                disconnect()
                onResult(false)
            }
        }
    }

    fun disconnect() {

        try {

            isConnected = false
            _connectedDevice.value = null
            outputStream?.close()
            outputStream = null
            socket?.close()
            socket = null

            Log.d("BluetoothClient", "Disconnected from server")

        } catch (e: IOException) {
            Log.e("BluetoothClient", "Close error: ${e.message}")
        }
    }
}