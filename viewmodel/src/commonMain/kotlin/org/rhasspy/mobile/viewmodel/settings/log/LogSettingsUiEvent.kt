package org.rhasspy.mobile.viewmodel.settings.log

import org.rhasspy.mobile.data.log.LogLevel
import org.rhasspy.mobile.viewmodel.settings.microphoneoverlay.MicrophoneOverlaySettingsUiEvent

sealed interface LogSettingsUiEvent {

    sealed interface Navigate : LogSettingsUiEvent {
        object BackClick: Navigate
    }

    sealed interface Change : LogSettingsUiEvent {
        data class SetLogLevel(val logLevel: LogLevel) : Change
        data class SetCrashlyticsEnabled(val enabled: Boolean) : Change
        data class SetShowLogEnabled(val enabled: Boolean) : Change
        data class SetLogAudioFramesEnabled(val enabled: Boolean) : Change
    }

}