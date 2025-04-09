package com.example.wearable.datalayer.ui

import android.R
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.wearable.common.data.Event

@Composable
fun MainApp(
    events: List<Event>,
    image: Bitmap?,
    apiAvailable: Boolean,
    cameraSupported: Boolean,
    onTakePhotoClick: () -> Unit,
    onSendPhotoClick: () -> Unit,
    onSendMessageClick: (String?) -> Unit,
    onStartWatchAppClick: () -> Unit
) {

    var message by remember { mutableStateOf("") }

    LazyColumn(contentPadding = PaddingValues(20.dp)) {

        // api 사용 불가능 시
        if (!apiAvailable) {

            item {
                Text(
                    text = "The Wearable API is not available on this device",
                    color = Color.Red,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Watch App에 입력한 텍스트 전송
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextField(
                    modifier = Modifier.weight(0.6f),
                    value = message,
                    onValueChange = { message = it },
                    label = { Text(text = "Send Message") },
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    modifier = Modifier.weight(0.2f),
                    onClick = { onSendMessageClick(message) }) {
                    Text(text = "Send")
                }
            }
            CommonDivider()
        }

        // Watch App에 촬영한 사진 전송
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {

                    // 사진 촬영 버튼 (카메라 지원하는 경우에 활성화)
                    Button(
                        modifier = Modifier.fillMaxWidth(0.9f),
                        enabled = cameraSupported,
                        onClick = onTakePhotoClick
                    ) {
                        Text(text = "Take Photo")
                    }

                    // 사진 전송 버튼 (카메라 지원 + 촬영 이미지 있는 경우에 활성화)
                    Button(
                        modifier = Modifier.fillMaxWidth(0.9f),
                        enabled = cameraSupported && image != null,
                        onClick = onSendPhotoClick
                    ) {
                        Text(text = "Send Photo")
                    }
                }
                Box(modifier = Modifier.size(100.dp)) {
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
            CommonDivider()
        }

        // Watch App 실행 버튼
        item {
            Button(
                modifier = Modifier.fillMaxWidth(1f),
                onClick = onStartWatchAppClick
            ) {
                Text(text = "Start Wearable Watch App")
            }
            CommonDivider()
        }

        // 수신 events 목록
        items(events) { event ->
            Column {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = event.text,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun CommonDivider() {
    Divider(Modifier.padding(0.dp, 12.dp, 0.dp, 12.dp).alpha(0.3f))
}

@Preview
@Composable
fun MainAppPreview() {
    MainApp(
        events = listOf(
            Event(
                title = "Event 1",
                text = "Event 1 test"
            )
        ),
        image = null,
        apiAvailable = true,
        cameraSupported = true,
        onTakePhotoClick = {},
        onSendPhotoClick = {},
        onSendMessageClick = {},
        onStartWatchAppClick = {}
    )
}