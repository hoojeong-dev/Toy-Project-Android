package com.example.wearable.datalayer.service

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
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

                CommonConstants.PATH_SEND_IMAGE -> {

                    // 이미지 가져오기
                    val asset = DataMapItem.fromDataItem(event.dataItem).dataMap.getAsset(CommonConstants.KEY_IMAGE) ?: return

                    coroutineScope.launch {

                        // intent로 이미지 전송 및 앱 실행
                        Intent(this@DataLayerListenerService, ActMain::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                            putExtra(CommonConstants.KEY_IMAGE, asset.toBitmap())
                            startActivity(this)
                        }
                    }
                }
            }
        }
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        super.onMessageReceived(messageEvent)

        when(messageEvent.path) {

            // watch app 실행
            CommonConstants.PATH_START_WATCH_APP -> {

                startActivity(
                    Intent(this@DataLayerListenerService, ActMain::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    }
                )
            }
        }
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