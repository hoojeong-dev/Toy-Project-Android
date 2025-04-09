package com.example.wearable.datalayer.ui.viewmodel

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.wearable.common.data.Event
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.CapabilityInfo
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent

/**
 * Main ViewModel
 */
class MainViewModel:
    ViewModel(),
    DataClient.OnDataChangedListener,
    MessageClient.OnMessageReceivedListener,
    CapabilityClient.OnCapabilityChangedListener {

    /** Events */
    private val _events = mutableStateListOf<Event>()
    val events: List<Event> = _events

    /** 촬영한 Image */
    var image by mutableStateOf<Bitmap?>(null)

    override fun onDataChanged(dataEvent: DataEventBuffer) {

        dataEvent.map { event ->

            val title = when(event.type) {

                DataEvent.TYPE_CHANGED -> "DataItem Changed"
                DataEvent.TYPE_DELETED -> "DataItem Deleted"
                else -> "Unknown DataItem Type"
            }

            _events.add(
                Event(
                    title = title,
                    text = event.dataItem.toString()
                )
            )
        }
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {

        _events.add(
            Event(
                title = "Message from watch",
                text = messageEvent.toString()
            )
        )
    }

    override fun onCapabilityChanged(capabilityInfo: CapabilityInfo) {

        _events.add(
            Event(
                title = "Capability Changed",
                text = capabilityInfo.toString()
            )
        )
    }

    /**
     * 촬영한 이미지 저장
     */
    fun saveTakenPhoto(bitmap: Bitmap?) {
        image = bitmap
    }
}