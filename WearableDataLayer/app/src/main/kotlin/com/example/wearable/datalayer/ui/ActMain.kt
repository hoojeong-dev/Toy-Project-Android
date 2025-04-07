package com.example.wearable.datalayer.ui

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import com.example.wearable.common.CommonConstants
import com.example.wearable.datalayer.ui.theme.WearableDataLayerTheme
import com.example.wearable.datalayer.ui.viewmodel.MainViewModel
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.AvailabilityException
import com.google.android.gms.common.api.GoogleApi
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * Main Activity
 */
class ActMain : ComponentActivity() {

    /** Wearable Client */
    private val dataClient by lazy { Wearable.getDataClient(this) }              // Wear OS 기기와 데이터 동기화
    private val messageClient by lazy { Wearable.getMessageClient(this) }        // Wear OS 기기와 단방향 통신
    private val capabilityClient by lazy { Wearable.getCapabilityClient(this) }  // Wear OS 기기의 특정 기능(예: 카메라 지원 여부) 확인

    /** ViewModel */
    private val mainViewModel by viewModels<MainViewModel>()

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

                MainApp(
                    events = mainViewModel.events,
                    apiAvailable = apiAvailable,
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
     * Watch APP 실행
     */
    private fun startWatchApp() {

        lifecycleScope.launch {

            try {

                // wearable 디바이스 정보 가져오기
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
}