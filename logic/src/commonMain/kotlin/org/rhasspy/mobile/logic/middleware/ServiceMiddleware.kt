package org.rhasspy.mobile.logic.middleware

import io.ktor.utils.io.core.Closeable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okio.Path
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject
import org.rhasspy.mobile.platformspecific.combineState
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.logic.services.dialog.DialogManagerService
import org.rhasspy.mobile.logic.services.dialog.DialogManagerServiceState
import org.rhasspy.mobile.logic.services.localaudio.LocalAudioService
import org.rhasspy.mobile.logic.services.mqtt.MqttService
import org.rhasspy.mobile.logic.services.settings.AppSettingsService
import org.rhasspy.mobile.logic.services.speechtotext.SpeechToTextService
import org.rhasspy.mobile.logic.services.texttospeech.TextToSpeechService
import org.rhasspy.mobile.logic.services.wakeword.WakeWordService
import org.rhasspy.mobile.logic.settings.AppSetting
import org.rhasspy.mobile.platformspecific.audioplayer.AudioSource

/**
 * handles ALL INCOMING events
 */
class ServiceMiddleware : KoinComponent, Closeable {

    private val dialogManagerService by inject<DialogManagerService>()
    private val speechToTextService by inject<SpeechToTextService>()
    private val appSettingsService by inject<AppSettingsService>()
    private val localAudioService by inject<LocalAudioService>()
    private val mqttService by inject<MqttService>()
    private val wakeWordService by inject<WakeWordService>()
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    private val _isPlayingRecording = MutableStateFlow(false)
    val isPlayingRecording = _isPlayingRecording.readOnly
    val isPlayingRecordingEnabled = combineState(_isPlayingRecording, dialogManagerService.currentDialogState) { playing, state ->
        playing || (state == DialogManagerServiceState.Idle || state == DialogManagerServiceState.AwaitingWakeWord)
    }

    val isUserActionEnabled = combineState(_isPlayingRecording, dialogManagerService.currentDialogState) { playingRecording, dialogState ->
        !playingRecording && (dialogState == DialogManagerServiceState.Idle ||
                dialogState == DialogManagerServiceState.AwaitingWakeWord ||
                dialogState == DialogManagerServiceState.RecordingIntent)
    }
    private var shouldResumeHotWordService = false

    fun action(action: Action) {
        coroutineScope.launch {
            when (action) {
                is Action.PlayStopRecording -> {
                    if (_isPlayingRecording.value) {
                        _isPlayingRecording.value = false
                        if (shouldResumeHotWordService) {
                            action(Action.AppSettingsAction.HotWordToggle(true))
                        }
                        action(Action.DialogAction.PlayFinished(Source.Local))
                    } else {
                        if (dialogManagerService.currentDialogState.value == DialogManagerServiceState.Idle ||
                            dialogManagerService.currentDialogState.value == DialogManagerServiceState.AwaitingWakeWord) {
                            _isPlayingRecording.value = true
                            shouldResumeHotWordService = AppSetting.isHotWordEnabled.value
                            action(Action.AppSettingsAction.HotWordToggle(false))
                            //suspend coroutine
                            localAudioService.playAudio(AudioSource.File(speechToTextService.speechToTextAudioFile))
                            //resumes when play finished
                            if (_isPlayingRecording.value) {
                                action(Action.PlayStopRecording)
                            }
                        }
                    }
                }

                is Action.WakeWordError -> mqttService.wakeWordError(action.description)
                is Action.AppSettingsAction -> {
                    when (action) {
                        is Action.AppSettingsAction.AudioOutputToggle ->
                            appSettingsService.audioOutputToggle(action.enabled)

                        is Action.AppSettingsAction.AudioVolumeChange ->
                            appSettingsService.setAudioVolume(action.volume)

                        is Action.AppSettingsAction.HotWordToggle -> {
                            appSettingsService.hotWordToggle(action.enabled)
                            if(action.enabled){
                                wakeWordService.startDetection()
                            } else {
                                wakeWordService.stopDetection()
                            }
                        }

                        is Action.AppSettingsAction.IntentHandlingToggle ->
                            appSettingsService.intentHandlingToggle(action.enabled)
                    }
                }

                is Action.SayText -> {
                    get<TextToSpeechService>().textToSpeech("", action.text)
                }

                is Action.DialogAction -> {
                    dialogManagerService.onAction(action)
                }
            }
        }
    }

    fun userSessionClick() {
        when (dialogManagerService.currentDialogState.value) {
            DialogManagerServiceState.AwaitingWakeWord -> {
                action(Action.DialogAction.WakeWordDetected(Source.Local, "manual"))
            }

            DialogManagerServiceState.RecordingIntent -> {
                action(Action.DialogAction.StopListening(Source.Local))
            }

            else -> {}
        }
    }

    fun getRecordedFile(): Path = speechToTextService.speechToTextAudioFile

    override fun close() {
        coroutineScope.cancel()
    }

}