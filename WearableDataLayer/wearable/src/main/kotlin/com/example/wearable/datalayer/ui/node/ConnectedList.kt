package com.example.wearable.datalayer.ui.node

import androidx.compose.runtime.Composable
import androidx.wear.compose.material.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import com.google.android.gms.wearable.Node
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults.ItemType
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState
import com.google.android.horologist.compose.material.Chip
import com.google.android.horologist.compose.material.ResponsiveListHeader

@OptIn(ExperimentalHorologistApi::class)
@Composable
fun ConnectedList(
    nodes: Set<Node>
) {

    val columnState = rememberResponsiveColumnState(
        contentPadding = ScalingLazyColumnDefaults.padding(
            first = ItemType.Text,
            last = ItemType.Chip
        )
    )

    ScreenScaffold(scrollState = columnState) {
        ScalingLazyColumn(columnState = columnState) {

            // Header
            item {
                ResponsiveListHeader {
                    Text(text = "Connected node list")
                }
            }

            // Connected Node List
            items(nodes.size) { index ->

                val node = nodes.elementAt(index)
                Chip(
                    label = node.displayName,
                    secondaryLabel = node.id,
                    onClick = {}
                )
            }
        }
    }
}

@WearPreviewDevices
@Composable
fun ConnectedListPreview() {

    ConnectedList(
        nodes = emptySet()
    )
}