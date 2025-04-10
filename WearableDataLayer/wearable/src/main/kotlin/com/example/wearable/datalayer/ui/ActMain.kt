package com.example.wearable.datalayer.ui

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.lifecycleScope
import com.example.wearable.common.constants.CommonConstants
import com.example.wearable.datalayer.ui.navigation.Navigation
import com.example.wearable.datalayer.ui.viewmodel.MainViewModel
import com.example.wearable.datalayer.ui.viewmodel.NodeViewModel
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Main Activity
 */
class ActMain : ComponentActivity() {

    /** Wearable Client */
    private val capabilityClient by lazy { Wearable.getCapabilityClient(this) }  // Wear OS 기기의 특정 기능(예: 카메라 지원 여부) 확인

    /** ViewModel */
    private val mainViewModel by viewModels<MainViewModel>()
    private val nodeViewModel by viewModels<NodeViewModel>()

    /** Data(DataLayer, Message) Broadcast Receiver */
    private val dataReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {

            val type = intent?.getStringExtra(CommonConstants.KEY_BROADCAST) ?: return
            when(type) {

                // 이미지 수신
                CommonConstants.PATH_SEND_IMAGE -> setImageFromIntent(intent)

                // 메세지 수신
                CommonConstants.PATH_SEND_MESSAGE -> setMessageFromIntent(intent)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeData()
        initialize()
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onStart() {
        super.onStart()

        // Broadcast 등록
        val intentFilter = IntentFilter(CommonConstants.ACTION_SEND_DATA)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(dataReceiver, intentFilter, RECEIVER_EXPORTED)
        } else {
            registerReceiver(dataReceiver, intentFilter)
        }
    }

    override fun onResume() {
        super.onResume()

        // Capability Client 등록
        capabilityClient.removeLocalCapability(CommonConstants.WEAR_CAPABILITY)
        capabilityClient.addLocalCapability(CommonConstants.WEAR_CAPABILITY)
    }

    override fun onStop() {
        super.onStop()

        // Broadcast 등록 해제
        unregisterReceiver(dataReceiver)
    }

    /**
     * Main Activity Data 초기화
     */
    private fun initializeData() {

        // 전달 받은 이미지 저장
        setImageFromIntent(intent)

        // 전달 받은 메세지 저장
        setMessageFromIntent(intent)

        // 연결된 노드 데이터 저장
        lifecycleScope.launch { nodeViewModel.fetchNodes(capabilityClient) }
    }

    /**
     * Main Activity 초기화
     */
    private fun initialize() {

        setTheme(android.R.style.Theme_DeviceDefault)
        setContent {

            val image by rememberUpdatedState(newValue = mainViewModel.image)

            Navigation(
                image = image,
                messages = mainViewModel.messages,
                nodes = nodeViewModel.nodes
            )
        }
    }

    /**
     * 전달받은 이미지 뷰 모델에 저장
     *
     * @param intent
     */
    private fun setImageFromIntent(intent: Intent) {

        val image =  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(CommonConstants.KEY_IMAGE, Bitmap::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<Bitmap?>(CommonConstants.KEY_IMAGE)
        } ?: return

        mainViewModel.setImage(image)
    }

    /**
     * 전달받은 메세지 뷰 모델에 저장
     *
     * @param intent
     */
    private fun setMessageFromIntent(intent: Intent) {

        val message = intent.getByteArrayExtra(CommonConstants.KEY_MESSAGE)?.toString(Charsets.UTF_8) ?: return
        val timestamp = intent.getLongExtra(CommonConstants.KEY_TIMESTAMP, 0L)

        mainViewModel.setMessage(message, timestamp.toDate())
    }

    /**
     * timestamp to Date
     *
     * @param
     */
    private fun Long.toDate(): String? {

        val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss").withZone(ZoneId.systemDefault())
        return formatter.format(Instant.ofEpochMilli(this))
    }
}