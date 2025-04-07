package com.example.wearable.datalayer.service

import android.content.Intent
import com.example.wearable.common.CommonConstants
import com.example.wearable.datalayer.ui.ActMain
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService

/**
 * DataLayer Wearable Listener Service
 */
class DataLayerListenerService: WearableListenerService() {

    override fun onMessageReceived(messageEvent: MessageEvent) {
        super.onMessageReceived(messageEvent)

        when(messageEvent.path) {

            // watch app 실행
            CommonConstants.PATH_START_WATCH_APP -> {

                startActivity(
                    Intent(this@DataLayerListenerService, ActMain::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                )
            }
        }
    }
}