package com.example.wearable.classicbluetooth.ui.viewmodel

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.ViewModel
import com.example.wearable.classicbluetooth.constants.BluetoothConstants
import com.example.wearable.classicbluetooth.data.Event
import com.example.wearable.classicbluetooth.util.toDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel(private val context: Context) : ViewModel() {

    /** 앱에서 수신한 Messages */
    private val _messages = MutableStateFlow<List<Event>>(emptyList())
    val messages: StateFlow<List<Event>> = _messages.asStateFlow()

    /** Message BroadcastReceiver */
    private val messageReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {

            // Service로 부터 메세지 수신
            if (intent?.action == BluetoothConstants.ACTION_MESSAGE_RECEIVED) {

                val message = intent.getStringExtra(BluetoothConstants.EXTRA_MESSAGE)
                message?.let {

                    val data = Event(
                        title = it,
                        text = System.currentTimeMillis().toDate().toString()
                    )
                    _messages.value = listOf(data) + _messages.value
                }
            }
        }
    }

    init { registerMessageReceiver() }

    /**
     * Register Message Receiver
     */
    private fun registerMessageReceiver() {

        val filter = IntentFilter(BluetoothConstants.ACTION_MESSAGE_RECEIVED)
        context.registerReceiver(messageReceiver, filter, Context.RECEIVER_EXPORTED)
    }

    override fun onCleared() {
        super.onCleared()

        context.unregisterReceiver(messageReceiver)
    }
}