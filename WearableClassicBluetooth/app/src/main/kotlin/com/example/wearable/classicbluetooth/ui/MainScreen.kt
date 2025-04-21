package com.example.wearable.classicbluetooth.ui

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.example.wearable.classicbluetooth.ui.viewmodel.MainViewModel

@SuppressLint("MissingPermission")
@Composable
fun MainScreen(viewModel: MainViewModel) {

    val pairedDevices by viewModel.pairedDevices.collectAsState()
    val selectedDevice by viewModel.selectedDevice.collectAsState()
    val messages by viewModel.messages.collectAsState()
    var inputText by remember { mutableStateOf("") }

    LazyColumn(contentPadding = PaddingValues(20.dp)) {

        // 연결 기기
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = if (selectedDevice != null) selectedDevice?.name ?: "" else "선택한 기기가 없습니다.",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.width(8.dp))
                DropdownMenuWithDevices(
                    devices = pairedDevices,
                    onDeviceSelected = { viewModel.selectDevice(it) }
                )
            }
            CommonDivider()
        }

        // Watch App에 입력한 텍스트 전송
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextField(
                    modifier = Modifier.weight(0.6f),
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("Send Message") }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    modifier = Modifier.weight(0.2f),
                    onClick = {
                        viewModel.sendMessage(inputText)
                        inputText = ""
                    }) {
                    Text(text = "Send")
                }
            }
            CommonDivider()
        }

        // Watch App에 전송한 텍스트 (전송 성공 시 업데이트)
        items(messages) { Text(it) }
    }
}

@SuppressLint("MissingPermission")
@Composable
fun DropdownMenuWithDevices(
    devices: List<BluetoothDevice>,
    onDeviceSelected: (BluetoothDevice) -> Unit
) {

    var expanded by remember { mutableStateOf(false) }

    Column {
        Box {
            Button(
                onClick = { expanded = true }) {
                Text(text = "Select")
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                devices.forEach { device ->
                    DropdownMenuItem(text = { Text(device.name ?: device.address) }, onClick = {
                        expanded = false
                        onDeviceSelected(device)
                    })
                }
            }
        }
    }
}

@Composable
fun CommonDivider() {
    Divider(Modifier.padding(0.dp, 12.dp, 0.dp, 12.dp).alpha(0.3f))
}
