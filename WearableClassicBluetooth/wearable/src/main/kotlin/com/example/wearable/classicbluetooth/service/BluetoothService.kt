package com.example.wearable.classicbluetooth.service

import android.R
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.wearable.classicbluetooth.constants.BluetoothConstants
import com.example.wearable.classicbluetooth.bluetooth.BluetoothServerManager
import com.example.wearable.classicbluetooth.ui.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class BluetoothService : Service() {

    /** Bluetooth */
    private lateinit var bluetoothManager: BluetoothServerManager

    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private var serverJob: Job? = null

    override fun onCreate() {
        super.onCreate()

        bluetoothManager = BluetoothServerManager(this@BluetoothService)

        createNotificationChannel()
        startForeground(BluetoothConstants.NOTIFICATION_ID, createNotification(), ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startServer()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()

        serverJob?.cancel()
        bluetoothManager.stopServer()
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

    private fun startServer() {

        serverJob?.cancel()
        serverJob = coroutineScope.launch {

            try {
                bluetoothManager.messages.collect { message ->
                    broadcastMessage(message)
                }
            } catch (e: Exception) {
                Log.e("BluetoothServer", "Error collecting messages: ${e.message}")
            }
        }

        bluetoothManager.startServer()
    }

    private fun broadcastMessage(message: String) {

        val intent = Intent(BluetoothConstants.ACTION_MESSAGE_RECEIVED)
        intent.putExtra(BluetoothConstants.EXTRA_MESSAGE, message)
        sendBroadcast(intent)
    }
}