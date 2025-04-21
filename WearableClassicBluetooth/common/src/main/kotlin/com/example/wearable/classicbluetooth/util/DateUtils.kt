package com.example.wearable.classicbluetooth.util

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter


/**
 * timestamp to Date
 *
 * @param
 */
fun Long.toDate(): String? {

    val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss").withZone(ZoneId.systemDefault())
    return formatter.format(Instant.ofEpochMilli(this))
}