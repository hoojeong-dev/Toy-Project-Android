package com.example.wearable.classicbluetooth.constants

import java.util.UUID

object BluetoothConstants {

    /**
     * Bluetooth
     */
    val MY_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    const val SERVICE_NAME = "BluetoothService"

    /**
     * Broadcast
     */
    const val ACTION_MESSAGE_RECEIVED = "com.example.wearable.classicbluetooth.MESSAGE_RECEIVED"
    const val EXTRA_MESSAGE = "message"

    /**
     * Notification
     */
    const val NOTIFICATION_ID = 1
    const val CHANNEL_ID = "BluetoothServerChannel"
    const val CHANNEL_NAME = "Bluetooth Server"
}