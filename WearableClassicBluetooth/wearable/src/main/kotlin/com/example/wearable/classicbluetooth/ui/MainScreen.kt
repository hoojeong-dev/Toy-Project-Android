package com.example.wearable.classicbluetooth.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Card
import androidx.wear.compose.material.MaterialTheme
import com.example.wearable.classicbluetooth.ui.viewmodel.MainViewModel

@Composable
fun MainScreen(viewModel: MainViewModel) {

    val messages by viewModel.messages.collectAsState()

    LazyColumn(modifier = Modifier.fillMaxSize().padding(start = 16.dp, end = 16.dp)) {

        // title
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Received\nMessages",
                style = MaterialTheme.typography.title3,
                textAlign = TextAlign.Center
            )
        }

        // 수신한 메세지
        items(messages) {
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                enabled = false,
                onClick = {}
            ) {
                Column {
                    Text(
                        text = it.title,
                        color = Color.White,
                        style = MaterialTheme.typography.body2
                    )
                    Text(
                        text = it.text,
                        color = Color.Gray,
                        style = MaterialTheme.typography.caption3
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}