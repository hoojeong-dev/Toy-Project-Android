package com.example.wearable.datalayer.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import com.example.wearable.common.CommonConstants
import com.example.wearable.datalayer.ui.theme.WearableDataLayerTheme
import com.google.android.gms.wearable.Wearable

class ActMain : ComponentActivity() {

    /** Wearable Client */
    private val dataClient by lazy { Wearable.getDataClient(this) }              // Wear OS 기기와 데이터 동기화
    private val messageClient by lazy { Wearable.getMessageClient(this) }        // Wear OS 기기와 단방향 통신
    private val capabilityClient by lazy { Wearable.getCapabilityClient(this) }  // Wear OS 기기의 특정 기능(예: 카메라 지원 여부) 확인

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)

        setContent {
            WearApp("Android")
        }
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
     * Wearable Client Listener 등록
     */
    private fun addWearableClientListener() {

//        dataClient.addListener(mainViewModel)
//        messageClient.addListener(mainViewModel)
//        capabilityClient.addListener(mainViewModel, Uri.parse("${CommonConstants.WEAR_CAPABILITY}://"), CapabilityClient.FILTER_REACHABLE)
        capabilityClient.addLocalCapability(CommonConstants.WEAR_CAPABILITY)
    }

    /**
     * Wearable Client Listener 제거
     */
    private fun removeWearableClientListener() {

//        dataClient.removeListener(mainViewModel)
//        messageClient.removeListener(mainViewModel)
//        capabilityClient.removeListener(mainViewModel)
    }
}


@Composable
fun WearApp(greetingName: String) {
    WearableDataLayerTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            contentAlignment = Alignment.Center
        ) {
            TimeText()
            Greeting(greetingName = greetingName)
        }
    }
}

@Composable
fun Greeting(greetingName: String) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        color = MaterialTheme.colors.primary,
        text = stringResource(com.example.wearable.datalayer.R.string.hello_world, greetingName)
    )
}