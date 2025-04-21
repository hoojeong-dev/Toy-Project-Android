package com.example.wearable.classicbluetooth.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.util.Log
import com.example.wearable.classicbluetooth.constants.BluetoothConstants
import com.example.wearable.classicbluetooth.util.hasBluetoothConnectPermission
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

@SuppressLint("MissingPermission")
class BluetoothServerManager(private val context: Context) {

    private val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private var serverSocket: BluetoothServerSocket? = null
    private var clientSocket: BluetoothSocket? = null

    /** Message */
    private val _messages = MutableSharedFlow<String>()
    val messages: SharedFlow<String> = _messages

    private var serverJob: Job? = null
    private var isRunning = false
    private var isConnected = false

    fun startServer() {

        if (!context.hasBluetoothConnectPermission()) return
        if (isRunning) return

        isRunning = true
        serverJob = CoroutineScope(Dispatchers.IO).launch {

            while (isRunning) {

                try {

                    if (!isConnected) {
                        startServerSocket()
                        acceptClientConnection()
                    }

                } catch (e: IOException) {
                    Log.e("BluetoothServer", "Server error: ${e.message}")
                    isConnected = false
                    delay(1000)
                }
            }
        }
    }

    private fun startServerSocket() {

        try {

            serverSocket?.close()
            serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(
                BluetoothConstants.SERVICE_NAME,
                BluetoothConstants.MY_UUID
            )

            Log.d("BluetoothServer", "Server socket created")

        } catch (e: IOException) {
            Log.e("BluetoothServer", "Failed to create server socket: ${e.message}")
            throw e
        }
    }

    private suspend fun acceptClientConnection() {

        try {

            Log.d("BluetoothServer", "Waiting for client connection...")
            clientSocket = serverSocket?.accept()

            Log.d("BluetoothServer", "Client connected: ${clientSocket?.remoteDevice?.address}")
            
            clientSocket?.let { socket ->
                isConnected = true
                handleClientConnection(socket)
            }

        } catch (e: IOException) {
            Log.e("BluetoothServer", "Failed to accept connection: ${e.message}")
            throw e
        }
    }

    private suspend fun handleClientConnection(socket: BluetoothSocket) {

        try {

            val reader = BufferedReader(InputStreamReader(socket.inputStream))
            
            while (isRunning && isConnected) {

                try {

                    val line = reader.readLine()
                    if (line == null) {
                        Log.d("BluetoothServer", "End of stream reached")
                        break
                    }
                    
                    Log.d("BluetoothServer", "Received message: $line")
                    _messages.emit(line)

                } catch (e: IOException) {
                    Log.e("BluetoothServer", "Read error: ${e.message}")
                    break
                }
            }
        } catch (e: IOException) {
            Log.e("BluetoothServer", "Connection error: ${e.message}")
        } finally {
            isConnected = false
            closeClientSocket()
        }
    }

    private fun closeClientSocket() {

        try {

            clientSocket?.close()
            clientSocket = null

        } catch (e: IOException) {
            Log.e("BluetoothServer", "Close client socket error: ${e.message}")
        }
    }

    fun stopServer() {

        isRunning = false
        isConnected = false
        serverJob?.cancel()
        closeAllSockets()
    }

    private fun closeAllSockets() {

        try {

            clientSocket?.close()
            serverSocket?.close()

            clientSocket = null
            serverSocket = null

        } catch (e: IOException) {
            Log.e("BluetoothServer", "Close error: ${e.message}")
        }
    }
}