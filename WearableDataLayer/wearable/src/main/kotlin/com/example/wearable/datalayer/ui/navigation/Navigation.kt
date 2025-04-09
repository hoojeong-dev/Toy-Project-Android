package com.example.wearable.datalayer.ui.navigation

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
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
    image: Bitmap?
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
                    image = image
                )
            }
        }
    }
}