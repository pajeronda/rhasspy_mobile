package org.rhasspy.mobile.viewModels

import co.touchlab.kermit.Logger
import dev.icerock.moko.mvvm.livedata.LiveData
import dev.icerock.moko.mvvm.livedata.MediatorLiveData
import dev.icerock.moko.mvvm.livedata.readOnly
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import dev.icerock.moko.resources.desc.StringDesc
import org.rhasspy.mobile.data.WakeWordOption
import org.rhasspy.mobile.logger.FileLogger
import org.rhasspy.mobile.nativeutils.MicrophonePermission
import org.rhasspy.mobile.nativeutils.OverlayPermission
import org.rhasspy.mobile.services.ServiceInterface
import org.rhasspy.mobile.settings.AppSettings
import org.rhasspy.mobile.settings.ConfigurationSettings

class HomeScreenViewModel : ViewModel() {
    private val logger = Logger.withTag(this::class.simpleName!!)

    private val isCurrentOverlayPermissionRequestRequired = MediatorLiveData(false)
    private val isCurrentMicrophonePermissionRequestRequired = MediatorLiveData(false)

    val isMicrophonePermissionRequestRequired: LiveData<Boolean> = isCurrentMicrophonePermissionRequestRequired.readOnly()
    val isOverlayPermissionRequestRequired: LiveData<Boolean> = isCurrentOverlayPermissionRequestRequired.readOnly()

    init {
        logger.v { "init" }

        AppSettings.languageOption.value.addObserver {
            StringDesc.localeType = StringDesc.LocaleType.Custom(it.code)
        }

        isCurrentOverlayPermissionRequestRequired.addSource(OverlayPermission.granted) {
            isCurrentOverlayPermissionRequestRequired.value = (!it && AppSettings.isWakeWordLightIndication.data)
        }
        isCurrentOverlayPermissionRequestRequired.addSource(AppSettings.isWakeWordLightIndication.value) {
            isCurrentOverlayPermissionRequestRequired.value = (it && !OverlayPermission.granted.value)
        }

        isCurrentMicrophonePermissionRequestRequired.addSource(MicrophonePermission.granted) {
            isCurrentMicrophonePermissionRequestRequired.value =
                (!it && ConfigurationSettings.wakeWordOption.data == WakeWordOption.Porcupine)
        }
        isCurrentMicrophonePermissionRequestRequired.addSource(ConfigurationSettings.wakeWordOption.currentValue) {
            isCurrentMicrophonePermissionRequestRequired.value = ((it == WakeWordOption.Porcupine) && !MicrophonePermission.granted.value)
        }
    }

    val isRecording = ServiceInterface.sessionRunning
    val isPlayingRecording = ServiceInterface.isPlayingRecording

    fun toggleSession() = ServiceInterface.toggleSession()

    fun playRecording() = ServiceInterface.playRecording()

    fun intentRecognition(text: String) = ServiceInterface.recognizeIntent(text)

    fun speakText(text: String) = ServiceInterface.say(text)

    fun saveAndApplyChanges() = ServiceInterface.saveAndApplyChanges()

    fun resetChanges() = ServiceInterface.resetChanges()

    fun exportLogFile() = FileLogger.exportLogFile()

    fun shareLogFile() = FileLogger.shareLogFile()


}