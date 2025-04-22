package com.example.wearable.classicbluetooth.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

const val REQUEST_CODE_BLUETOOTH = 1001

fun Context.hasBluetoothConnectPermission(): Boolean {
    return ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
}

fun Context.hasBluetoothScanPermission(): Boolean {
    return ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED
}


fun Context.hasNotificationPermission(): Boolean {
    return ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
}
fun Activity.requestBluetoothPermissions(requestCode: Int) {
    val permissions = mutableListOf<String>()
    if (!hasBluetoothConnectPermission()) {
        permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
    }
    if (!hasBluetoothScanPermission()) {
        permissions.add(Manifest.permission.BLUETOOTH_SCAN)
    }
    if (!hasNotificationPermission()) {
        permissions.add(Manifest.permission.POST_NOTIFICATIONS)
    }
    if (permissions.isNotEmpty()) {
        ActivityCompat.requestPermissions(this, permissions.toTypedArray(), requestCode)
    }
}

val requiredPermissions = arrayOf(
    Manifest.permission.BLUETOOTH,
    Manifest.permission.BLUETOOTH_ADMIN,
    Manifest.permission.BLUETOOTH_CONNECT,
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.FOREGROUND_SERVICE,
    Manifest.permission.WAKE_LOCK,
    Manifest.permission.POST_NOTIFICATIONS
)

fun Context.hasAllPermissions(): Boolean {
    return requiredPermissions.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }
}