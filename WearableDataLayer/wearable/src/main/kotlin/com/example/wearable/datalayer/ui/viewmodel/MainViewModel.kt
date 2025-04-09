package com.example.wearable.datalayer.ui.viewmodel

import android.graphics.Bitmap
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.wearable.common.data.Event

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
}