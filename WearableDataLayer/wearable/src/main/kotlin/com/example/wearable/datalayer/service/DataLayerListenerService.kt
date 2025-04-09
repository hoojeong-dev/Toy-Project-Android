package com.example.wearable.datalayer.service

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.wearable.common.constants.CommonConstants
import com.example.wearable.datalayer.ui.ActMain
import com.google.android.gms.wearable.Asset
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.WearableListenerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * DataLayer Wearable Listener Service
 */
class DataLayerListenerService: WearableListenerService() {

    /** Coroutine Scope */
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        super.onDataChanged(dataEvents)

        dataEvents.map { event ->

            val uri = event.dataItem.uri
            when(uri.path) {

                // 이미지 수신
                CommonConstants.PATH_SEND_IMAGE -> {

                    // 이미지 가져오기
                    val asset = DataMapItem.fromDataItem(event.dataItem).dataMap.getAsset(CommonConstants.KEY_IMAGE) ?: return

                    coroutineScope.launch {

                        if (isForeground()) {

                            // 앱이 실행 중인 경우 - broadcast로 이미지 전송
                            Intent(CommonConstants.ACTION_SEND_DATA).apply {
                                putExtra(CommonConstants.KEY_BROADCAST, CommonConstants.PATH_SEND_IMAGE)
                                putExtra(CommonConstants.KEY_IMAGE, asset.toBitmap())
                                sendBroadcast(this)
                            }

                        } else {

                            // 앱이 꺼져있는 경우 - intent로 앱 실행 및 이미지 전송
                            mainIntent().apply {
                                putExtra(CommonConstants.KEY_IMAGE, asset.toBitmap())
                                startActivity(this)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        super.onMessageReceived(messageEvent)

        when(messageEvent.path) {

            // watch app 실행 (앱이 꺼져있는 경우)
            CommonConstants.PATH_START_WATCH_APP -> if (!isForeground()) startActivity(mainIntent())
        }
    }

    /**
     * ActMain Intent
     *
     * @return Intent
     */
    @SuppressLint("WearRecents")
    private fun mainIntent(): Intent =
        Intent(this@DataLayerListenerService, ActMain::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

    /**
     * 현재 앱이 Foreground 인지 확인
     *
     * @return Boolean
     */
    private fun isForeground(): Boolean {

        val activityManager = applicationContext.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val appProcesses = activityManager.runningAppProcesses ?: return false

        return appProcesses.any {
            it.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND &&
                    it.processName == packageName
        }


        return false
    }

    /**
     * Asset -> Bitmap
     *
     * @return Asset
     */
    private suspend fun Asset.toBitmap(): Bitmap = withContext(Dispatchers.Default) {

        val inputStream = Wearable.getDataClient(this@DataLayerListenerService)
            .getFdForAsset(this@toBitmap).await().inputStream
        BitmapFactory.decodeStream(inputStream)
    }
}