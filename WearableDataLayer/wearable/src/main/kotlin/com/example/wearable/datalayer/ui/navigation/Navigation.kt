package com.example.wearable.datalayer.ui.navigation

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.example.wearable.common.data.Event
import com.example.wearable.datalayer.ui.main.MainApp
import com.example.wearable.datalayer.ui.node.ConnectedList
import com.google.android.gms.wearable.Node
import com.google.android.horologist.compose.layout.AppScaffold

@Composable
fun Navigation(
    image: State<Bitmap?>,
    messages: List<Event>,
    nodes: Set<Node>,
    sendMessageToAndroid: (Context, String) -> Unit
) {

    AppScaffold {

        val navController = rememberSwipeDismissableNavController()
        SwipeDismissableNavHost(
            navController = navController,
            startDestination = Destination.DESTINATION_MAIN
        ) {

            // main 화면
            composable(route = Destination.DESTINATION_MAIN) {
                MainApp(
                    image = image.value,
                    messages = messages,
                    onShowNodeList = { navController.navigate(Destination.DESTINATION_CONNECTED_LIST) },
                    sendMessageToAndroid = sendMessageToAndroid
                )
            }

            // connected list 화면
            composable(route = Destination.DESTINATION_CONNECTED_LIST) {
                ConnectedList(
                    nodes = nodes
                )
            }
        }
    }
}