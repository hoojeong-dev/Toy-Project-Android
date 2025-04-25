package com.example.wearable.datalayer.service

import android.widget.Toast
import com.example.wearable.common.constants.CommonConstants
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

/**
 * DataLayer Wearable Listener Service
 */
class DataLayerListenerService: WearableListenerService() {

    /** Coroutine Scope */
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        super.onDataChanged(dataEvents)
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        super.onMessageReceived(messageEvent)

        when(messageEvent.path) {

            // message 수신
            CommonConstants.PATH_SEND_MESSAGE_FROM_WATCH -> {

                Toast.makeText(this, messageEvent.data.toString(Charsets.UTF_8), Toast.LENGTH_SHORT).show()
            }
        }
    }
}