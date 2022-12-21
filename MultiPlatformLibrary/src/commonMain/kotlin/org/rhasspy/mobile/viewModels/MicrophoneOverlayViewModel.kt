package org.rhasspy.mobile.viewModels

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.Application
import org.rhasspy.mobile.combineState
import org.rhasspy.mobile.data.MicrophoneOverlaySizeOptions
import org.rhasspy.mobile.mapReadonlyState
import org.rhasspy.mobile.nativeutils.MicrophonePermission
import org.rhasspy.mobile.nativeutils.OverlayPermission
import org.rhasspy.mobile.services.dialogManager.DialogManagerServiceState
import org.rhasspy.mobile.services.dialogManager.IDialogManagerService
import org.rhasspy.mobile.settings.AppSettings
import kotlin.math.roundToInt

class MicrophoneOverlayViewModel : ViewModel(), KoinComponent {

    private val dialogManagerServiceState = get<IDialogManagerService>().currentDialogState

    val shouldOverlayBeShown = combineState(
        OverlayPermission.granted,
        AppSettings.microphoneOverlaySizeOption.data,
        Application.Instance.isAppInBackground,
        AppSettings.isMicrophoneOverlayWhileAppEnabled.data
    ) { permissionGranted, microphoneOverlaySizeOption, isAppInBackground, isMicrophoneOverlayWhileApp ->
        permissionGranted && microphoneOverlaySizeOption != MicrophoneOverlaySizeOptions.Disabled && (isAppInBackground || isMicrophoneOverlayWhileApp)
    }

    val microphoneOverlayPositionX: Int get() = AppSettings.microphoneOverlayPositionX.value
    val microphoneOverlayPositionY: Int get() = AppSettings.microphoneOverlayPositionY.value

    val microphoneOverlaySize = AppSettings.microphoneOverlaySizeOption.data.mapReadonlyState { it.size }

    val isActionEnabled = dialogManagerServiceState
        .mapReadonlyState { it == DialogManagerServiceState.Idle || it == DialogManagerServiceState.AwaitingHotWord }
    val isShowBorder = dialogManagerServiceState.mapReadonlyState { it == DialogManagerServiceState.AwaitingHotWord }
    val isShowMicOn: StateFlow<Boolean> = MicrophonePermission.granted
    val isRecording =  dialogManagerServiceState.mapReadonlyState { it == DialogManagerServiceState.RecordingIntent }

    fun updateMicrophoneOverlayPosition(offsetX: Float, offsetY: Float) {
        AppSettings.microphoneOverlayPositionX.value = (((AppSettings.microphoneOverlayPositionX.value + offsetX).roundToInt()))
        AppSettings.microphoneOverlayPositionY.value = (((AppSettings.microphoneOverlayPositionY.value + offsetY).roundToInt()))
    }

    fun onClick() {
        //TODO open app and prompt permission request when no permission else start recording
    }

}