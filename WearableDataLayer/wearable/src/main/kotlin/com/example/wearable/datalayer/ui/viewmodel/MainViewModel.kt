package com.example.wearable.datalayer.ui.viewmodel

import android.graphics.Bitmap
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.wearable.common.data.Event
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.CapabilityInfo
import com.google.android.gms.wearable.DataClient
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

    /** Event */
    private val _events = mutableStateListOf<Event>()
    val events: List<Event> = _events

    /** 전달 받은 이미지 */
    private val _image = mutableStateOf<Bitmap?>(null)
    val image: State<Bitmap?> get() = _image

    override fun onDataChanged(p0: DataEventBuffer) {

    }

    override fun onMessageReceived(p0: MessageEvent) {

    }

    override fun onCapabilityChanged(p0: CapabilityInfo) {

    }

    /**
     * 수신한 이미지 저장
     *
     * @param image
     */
    fun setImage(image: Bitmap?) {
        _image.value = image
    }
}