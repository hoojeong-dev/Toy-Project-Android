package com.example.wearable.datalayer.ui.viewmodel

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wearable.common.constants.CommonConstants
import com.example.wearable.common.data.Event
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * Main ViewModel
 */
class MainViewModel: ViewModel() {

    /** 전달 받은 이미지 */
    private val _image = mutableStateOf<Bitmap?>(null)
    val image: State<Bitmap?> get() = _image

    /** 전달 받은 메세지 */
    private val _messages = mutableStateListOf<Event>()
    val messages: List<Event> = _messages

    /**
     * 수신한 이미지 저장
     *
     * @param image
     */
    fun setImage(image: Bitmap?) {
        _image.value = image
    }

    /**
     * 수신한 메세지 저장
     *
     * @param message
     * @param date
     */
    fun setMessage(message: String?, date: String?) {
        _messages.add(0,
            Event(
                title = message ?: "",
                text = date ?: ""
            )
        )
    }

    /**
     * Android로 메시지 전송
     *
     * @param context
     * @param message
     */
    fun sendMessageToAndroid(context: Context, message: String) {
        viewModelScope.launch {
            try {
                val messageClient = Wearable.getMessageClient(context)
                val capabilityClient = Wearable.getCapabilityClient(context)

                capabilityClient
                    .getCapability(CommonConstants.WEAR_CAPABILITY, CapabilityClient.FILTER_REACHABLE)
                    .await()
                    .nodes
                    .forEach { node ->
                        messageClient.sendMessage(
                            node.id,
                            CommonConstants.PATH_SEND_MESSAGE_FROM_WATCH,
                            message.toByteArray()
                        ).await()
                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}