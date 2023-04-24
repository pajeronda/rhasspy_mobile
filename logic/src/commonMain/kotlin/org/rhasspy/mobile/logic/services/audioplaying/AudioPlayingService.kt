package org.rhasspy.mobile.logic.services.audioplaying

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.inject
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.data.service.option.AudioPlayingOption
import org.rhasspy.mobile.logic.logger.LogType
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction
import org.rhasspy.mobile.logic.middleware.Source
import org.rhasspy.mobile.logic.services.IService
import org.rhasspy.mobile.logic.services.httpclient.HttpClientService
import org.rhasspy.mobile.logic.services.localaudio.LocalAudioService
import org.rhasspy.mobile.logic.services.mqtt.MqttService
import org.rhasspy.mobile.platformspecific.audioplayer.AudioSource
import org.rhasspy.mobile.platformspecific.readOnly

/**
 * calls actions and returns result
 *
 * when data is null the service was most probably mqtt and will return result in a call function
 */
open class AudioPlayingService(
    paramsCreator: AudioPlayingServiceParamsCreator
) : IService(LogType.AudioPlayingService) {

    private val localAudioService by inject<LocalAudioService>()
    private val httpClientService by inject<HttpClientService>()
    private val mqttClientService by inject<MqttService>()

    private val _serviceState = MutableStateFlow<ServiceState>(ServiceState.Success)
    override val serviceState = _serviceState.readOnly

    private var paramsFlow: StateFlow<AudioPlayingServiceParams> = paramsCreator()
    private val params: AudioPlayingServiceParams get() = paramsFlow.value

    /**
     * hermes/audioServer/<siteId>/playBytes/<requestId>
     * Play WAV data
     *
     * Response(s)
     * hermes/audioServer/<siteId>/playFinished (JSON)
     *
     * - if audio output is enabled
     *
     * Local:
     * - play audio with volume set
     *
     * HTTP:
     * - calls service to play audio with wav data
     *
     * MQTT:
     * - calls default site to play audio
     */
    suspend fun playAudio(audioSource: AudioSource) {
        logger.d { "playAudio dataSize: $audioSource" }
        when (params.audioPlayingOption) {
            AudioPlayingOption.Local -> {
                _serviceState.value = localAudioService.playAudio(audioSource)
                serviceMiddleware.action(DialogServiceMiddlewareAction.PlayFinished(Source.Local))
            }

            AudioPlayingOption.RemoteHTTP -> {
                _serviceState.value = httpClientService.playWav(audioSource).toServiceState()
                serviceMiddleware.action(DialogServiceMiddlewareAction.PlayFinished(Source.HttpApi))
            }

            AudioPlayingOption.RemoteMQTT -> {
                _serviceState.value = mqttClientService.playAudioRemote(audioSource)
                serviceMiddleware.action(DialogServiceMiddlewareAction.PlayFinished(Source.Local))
            }

            AudioPlayingOption.Disabled -> {}
        }
    }

    /**
     * stops playing audio when aduio is played locally
     */
    fun stopPlayAudio() {
        when (params.audioPlayingOption) {
            AudioPlayingOption.Local -> localAudioService.stop()
            else -> {}
        }
    }

}