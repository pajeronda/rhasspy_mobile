package org.rhasspy.mobile.platformspecific.background

import kotlin.native.concurrent.ThreadLocal

/**
 * Native Service to run continuously in background
 */
expect class BackgroundService() {

    @ThreadLocal
    companion object {

        /**
         * start background service
         */
        fun start()

        /**
         * stop background work
         */
        fun stop()

    }

}