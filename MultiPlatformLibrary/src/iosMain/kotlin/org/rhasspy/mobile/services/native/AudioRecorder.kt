package org.rhasspy.mobile.services.native

import kotlinx.coroutines.flow.MutableSharedFlow

actual object AudioRecorder {

    actual val output = MutableSharedFlow<ByteArray>()

    actual fun startRecording() {
        TODO("Not yet implemented")
    }

    actual fun stopRecording() {
        TODO("Not yet implemented")
    }

}