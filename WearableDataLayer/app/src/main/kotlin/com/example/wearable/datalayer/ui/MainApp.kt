package com.example.wearable.datalayer.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.wearable.datalayer.ui.viewmodel.Event

@Composable
fun MainApp(
    events: List<Event>,
    apiAvailable: Boolean,
    onStartWatchAppClick: () -> Unit
) {

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

//        item {
//            Row(verticalAlignment = Alignment.CenterVertically) {
//
//                Column(Modifier.weight(1f)) {
//
//                }
//            }
//        }

        // Watch App 실행 버튼
        item {
            Button(onClick = onStartWatchAppClick) {
                Text(text = "Start Wearable Watch App")
            }
            Spacer(modifier = Modifier.height(12.dp))
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
        apiAvailable = true,
        onStartWatchAppClick = {}
    )
}