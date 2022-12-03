package org.rhasspy.mobile.viewModels.configuration.test

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.component.get
import org.rhasspy.mobile.data.SpeechToTextOptions
import org.rhasspy.mobile.middleware.IServiceMiddleware
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.services.ServiceResponse
import org.rhasspy.mobile.services.mqtt.MqttService
import org.rhasspy.mobile.services.rhasspyactions.RhasspyActionsService
import org.rhasspy.mobile.services.rhasspyactions.RhasspyActionsServiceParams

class SpeechToTextConfigurationTest : IConfigurationTest() {

    private val _isRecording = MutableStateFlow(false)
    val isRecording = _isRecording.readOnly

    fun toggleRecording() {
        testScope.launch {
            if (get<RhasspyActionsServiceParams>().speechToTextOption == SpeechToTextOptions.RemoteMQTT) {
                //await for mqtt service to start if necessary
                get<MqttService>()
                    .isHasStarted
                    .map { it }
                    .distinctUntilChanged()
                    .first { it }
            }

            //await for mqtt to be started
            val rhasspyActionsService = get<RhasspyActionsService>()
            val middleware = get<IServiceMiddleware>()

            if (!isRecording.value) {
                _isRecording.value = true
                testScope.launch {
                    rhasspyActionsService.startSpeechToText(middleware.sessionId)
                }
            } else {
                _isRecording.value = false
                //execute
                testScope.launch {
                    val response = rhasspyActionsService.endSpeechToText(middleware.sessionId)

                    if (response is ServiceResponse.Success) {
                        println(response.data)
                    }
                }
            }
        }
    }

    override fun onClose() {}

}