package com.example.wearable.common.constants

class CommonConstants {

    companion object {

        const val WEAR_CAPABILITY = "wear"

        /**
         * Path
         */
        const val PATH_SEND_IMAGE = "/image"
        const val PATH_SEND_MESSAGE = "/message"
        const val PATH_START_WATCH_APP = "/start_watch_app"

        /**
         * Key
         */
        const val KEY_IMAGE = "KEY_IMAGE"
        const val KEY_TIMESTAMP = "KEY_TIMESTAMP"
        const val KEY_BROADCAST = "KEY_BROADCAST"

        /**
         * Broadcast
         */
        const val ACTION_SEND_DATA = "com.example.wearable.ACTION_MESSAGE_RECEIVED"
    }
}