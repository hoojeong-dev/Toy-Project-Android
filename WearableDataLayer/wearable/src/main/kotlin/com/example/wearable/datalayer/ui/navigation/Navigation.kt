package com.example.wearable.datalayer.ui.navigation

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.example.wearable.common.data.Event
import com.example.wearable.datalayer.ui.main.MainApp
import com.google.android.horologist.compose.layout.AppScaffold

@Composable
fun Navigation(
    image: State<Bitmap?>,
    messages: List<Event>
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
                    messages = messages
                )
            }
        }
    }
}