package com.example.wearable.datalayer.ui

import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.wearable.common.constants.CommonConstants
import com.example.wearable.datalayer.ui.navigation.Navigation
import com.example.wearable.datalayer.ui.viewmodel.MainViewModel
import com.example.wearable.datalayer.ui.viewmodel.NodeViewModel
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.launch

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

        initializeData()
        initialize()
    }

    override fun onResume() {
        super.onResume()

        // Capability Client 등록
        capabilityClient.removeLocalCapability(CommonConstants.WEAR_CAPABILITY)
        capabilityClient.addLocalCapability(CommonConstants.WEAR_CAPABILITY)
    }

    /**
     * Main Activity Data 초기화
     */
    private fun initializeData() {

        // 전달 받은 이미지 저장
        mainViewModel.image = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(CommonConstants.KEY_IMAGE, Bitmap::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<Bitmap?>(CommonConstants.KEY_IMAGE)
        }
    }

    /**
     * Main Activity 초기화
     */
    private fun initialize() {

        setTheme(android.R.style.Theme_DeviceDefault)
        setContent {

            Navigation(
                image = mainViewModel.image
            )
        }
    }
}