package com.example.wearable.datalayer.ui.main

import android.R
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.material.Card
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
import com.example.wearable.common.data.Event
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults.ItemType
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState
import com.google.android.horologist.compose.material.Chip

@OptIn(ExperimentalHorologistApi::class)
@Composable
fun MainApp(
    image: Bitmap?,
    messages: List<Event>,
    onShowNodeList: () -> Unit
) {

    val columnState = rememberResponsiveColumnState(
        contentPadding = ScalingLazyColumnDefaults.padding(
            first = ItemType.Chip,
            last = ItemType.Text
        )
    )

    ScreenScaffold(scrollState = columnState) {
        ScalingLazyColumn(columnState = columnState) {

            // 연결된 기기 목록
            item {
                Chip(
                    label = "Query for connected devices",
                    onClick = onShowNodeList
                )
            }

            // 수신 이미지
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().aspectRatio(1f).padding(24.dp)
                ) {
                    if (image == null) {
                        Image(
                            modifier = Modifier.fillMaxSize(),
                            painter = painterResource(id = R.mipmap.sym_def_app_icon),
                            contentDescription = "Sample Image"
                        )
                    } else {
                        Image(
                            modifier = Modifier.fillMaxSize(),
                            bitmap = image.asImageBitmap(),
                            contentDescription = "Captured Image"
                        )
                    }
                }
            }

            // 수신 message 목록
            if (messages.isEmpty()) {
                item {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Waiting for message to arrived",
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                items(messages) { event ->
                    Card(
                        enabled = false,
                        onClick = {}
                    ) {
                        Column {
                            Text(
                                text = event.title,
                                style = MaterialTheme.typography.title3
                            )
                            Text(
                                text = event.text,
                                style = MaterialTheme.typography.body2
                            )
                        }
                    }
                }
            }
        }
    }
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun MainAppPreview() {

    MainApp(
        image = null,
        messages = emptyList(),
        onShowNodeList = {}
    )
}