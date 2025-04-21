package com.example.wearable.classicbluetooth.service

import android.R
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.wearable.classicbluetooth.constants.BluetoothConstants
import com.example.wearable.classicbluetooth.bluetooth.BluetoothServerManager
import com.example.wearable.classicbluetooth.ui.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BluetoothService : Service() {

    /** Bluetooth */
    private lateinit var bluetoothManager: BluetoothServerManager

    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private var serverJob: Job? = null
    private var monitorJob: Job? = null
    private var isServerRunning = false

    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService(): BluetoothService = this@BluetoothService
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("BluetoothService", "Service onCreate")

        bluetoothManager = BluetoothServerManager(this@BluetoothService)

        createNotificationChannel()
        startForeground(BluetoothConstants.NOTIFICATION_ID, createNotification(), ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        
        startServerMonitor()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("BluetoothService", "Service onStartCommand")
        startServer()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
        Log.d("BluetoothService", "Service rebound")
        startServer()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d("BluetoothService", "Service unbound")
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("BluetoothService", "Service onDestroy")

        serverJob?.cancel()
        monitorJob?.cancel()
        bluetoothManager.stopServer()
    }

    private fun startServerMonitor() {

        monitorJob?.cancel()
        monitorJob = coroutineScope.launch {

            while (true) {

                if (!isServerRunning) {
                    Log.d("BluetoothService", "Server not running, attempting to restart...")
                    startServer()
                }

                // 5초마다 연결 체크
                delay(5000)
            }
        }
    }

    private fun startServer() {
        Log.d("BluetoothService", "Starting server...")
        
        serverJob?.cancel()
        serverJob = coroutineScope.launch {

            try {
                bluetoothManager.messages.collect { message ->
                    Log.d("BluetoothService", "Message received: $message")
                    broadcastMessage(message)
                }
            } catch (e: Exception) {
                Log.e("BluetoothServer", "Error collecting messages: ${e.message}")
                isServerRunning = false
            }
        }

        try {
            bluetoothManager.startServer()
            isServerRunning = true
            Log.d("BluetoothService", "Server started successfully")
        } catch (e: Exception) {
            Log.e("BluetoothService", "Failed to start server: ${e.message}")
            isServerRunning = false
        }
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(BluetoothConstants.CHANNEL_ID, BluetoothConstants.CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun createNotification(): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(this, BluetoothConstants.CHANNEL_ID)
            .setContentTitle("Bluetooth 서버 실행 중")
            .setContentText("메시지 수신 대기 중...")
            .setSmallIcon(R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun broadcastMessage(message: String) {

        val intent = Intent(BluetoothConstants.ACTION_MESSAGE_RECEIVED)
        intent.putExtra(BluetoothConstants.EXTRA_MESSAGE, message)
        sendBroadcast(intent)
    }
}