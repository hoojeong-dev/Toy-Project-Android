package com.example.wearable.datalayer.ui

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.wearable.common.constants.CommonConstants
import com.example.wearable.datalayer.ui.theme.WearableDataLayerTheme
import com.example.wearable.datalayer.ui.viewmodel.MainViewModel
import com.example.wearable.datalayer.ui.viewmodel.MainViewModelFactory
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.AvailabilityException
import com.google.android.gms.common.api.GoogleApi
import com.google.android.gms.wearable.Asset
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

/**
 * Main Activity
 */
class ActMain : ComponentActivity() {

    /** Wearable Client */
    private val dataClient by lazy { Wearable.getDataClient(this) }              // Wear OS 기기와 데이터 동기화
    private val messageClient by lazy { Wearable.getMessageClient(this) }        // Wear OS 기기와 단방향 통신
    private val capabilityClient by lazy { Wearable.getCapabilityClient(this) }  // Wear OS 기기의 특정 기능(예: 카메라 지원 여부) 확인

    /** ViewModel */
    private val mainViewModel by lazy {
        ViewModelProvider(this, MainViewModelFactory(capabilityClient))[MainViewModel::class.java]
    }

    /** Camera 지원 여부 */
    private val cameraSupported by lazy {
        packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
    }

    /** 사진 촬영 런처 */
    private val takePhotoLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { result ->
        mainViewModel.saveTakenPhoto(result)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
    }

    override fun onResume() {
        super.onResume()
        addWearableClientListener()
    }

    override fun onPause() {
        super.onPause()
        removeWearableClientListener()
    }

    /**
     * Main Activity 초기화
     */
    private fun initialize() {

        setContent {

            WearableDataLayerTheme {

                var apiAvailable by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) {
                    apiAvailable = isAvailableAPI(capabilityClient)
                }

                val nodes by mainViewModel.nodes.collectAsState()

                MainApp(
                    events = mainViewModel.events,
                    image = mainViewModel.image,
                    nodes = nodes,
                    apiAvailable = apiAvailable,
                    cameraSupported = cameraSupported,
                    onTakePhotoClick = ::takePhoto,
                    onSendPhotoClick = ::sendPhoto,
                    onSendMessageClick = ::sendMessage,
                    onStartWatchAppClick = ::startWatchApp
                )
            }
        }
    }

    /**
     * Wearable Client Listener 등록
     */
    private fun addWearableClientListener() {

        dataClient.addListener(mainViewModel)
        messageClient.addListener(mainViewModel)
        capabilityClient.addListener(mainViewModel, Uri.parse("${CommonConstants.WEAR_CAPABILITY}://"), CapabilityClient.FILTER_REACHABLE)

        capabilityClient.removeLocalCapability(CommonConstants.WEAR_CAPABILITY)
        capabilityClient.addLocalCapability(CommonConstants.WEAR_CAPABILITY)
    }

    /**
     * Wearable Client Listener 제거
     */
    private fun removeWearableClientListener() {

        dataClient.removeListener(mainViewModel)
        messageClient.removeListener(mainViewModel)
        capabilityClient.removeListener(mainViewModel)
    }

    /**
     * 사진 촬영
     */
    private fun takePhoto() {

        if (!cameraSupported) return
        takePhotoLauncher.launch(null)
    }

    /**
     * 사진 전송
     */
    private fun sendPhoto() {

        if (!cameraSupported) return

        lifecycleScope.launch {

            try {

                // 촬영한 이미지 DataClient 사용하여 전송
                val image = mainViewModel.image ?: return@launch
                val request = PutDataMapRequest.create(CommonConstants.PATH_SEND_IMAGE).apply {
                    dataMap.putAsset(CommonConstants.KEY_IMAGE, image.toAsset())
                    dataMap.putLong(CommonConstants.KEY_TIMESTAMP, System.currentTimeMillis())
                }.asPutDataRequest().setUrgent()

                val result = dataClient.putDataItem(request).await()
                Log.d("!@", "Send photo result: $result")

            } catch (e: Exception) {
                Log.d("!@", "Send photo failed: $e")
            }
        }
    }

    /**
     * 입력한 메세지 전송
     */
    private fun sendMessage(message: String?) {

        lifecycleScope.launch {

            try {

                // 특정 wearable 디바이스 찾아서 PATH_START_WATCH_APP로 빈 값 전송
                capabilityClient
                    .getCapability(CommonConstants.WEAR_CAPABILITY, CapabilityClient.FILTER_REACHABLE)
                    .await()
                    .nodes
                    .map { node ->
                        async { messageClient.sendMessage(node.id, CommonConstants.PATH_SEND_MESSAGE, message?.toByteArray()).await() }
                    }.awaitAll()

            } catch (e: Exception) {
                Log.d("!@", "Send message failed: $e")
            }
        }
    }

    /**
     * Watch APP 실행
     */
    private fun startWatchApp() {

        lifecycleScope.launch {

            try {

                // 특정 wearable 디바이스 찾아서 PATH_START_WATCH_APP로 빈 값 전송
                capabilityClient
                    .getCapability(CommonConstants.WEAR_CAPABILITY, CapabilityClient.FILTER_REACHABLE)
                    .await()
                    .nodes
                    .map { node ->
                        async { messageClient.sendMessage(node.id, CommonConstants.PATH_START_WATCH_APP, byteArrayOf()).await() }
                    }.awaitAll()

            } catch (e: Exception) {
                Log.d("!@", "Starting activity failed: $e")
            }
        }
    }

    /**
     * Wearable API 사용 가능 여부 체크
     * - dataClient, messageClient, capabilityClient의 API를 지원하는 지 확인
     *
     * @param api
     * @return Boolean
     */
    private suspend fun isAvailableAPI(api: GoogleApi<*>): Boolean {

        return try {

            GoogleApiAvailability.getInstance().checkApiAvailability(api).await()
            true

        } catch (e: AvailabilityException) {
            Log.d("!@", "API is not available in this device: $e")
            false
        }
    }

    /**
     * Bitmap -> Asset
     *
     * @return Asset
     */
    private suspend fun Bitmap.toAsset(): Asset = withContext(Dispatchers.Default) {

        ByteArrayOutputStream().use { byteStream ->
            compress(Bitmap.CompressFormat.PNG, 100, byteStream)
            Asset.createFromBytes(byteStream.toByteArray())
        }
    }
}