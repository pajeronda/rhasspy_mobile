package org.rhasspy.mobile.viewModels.configuration.test

import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.component.get
import org.rhasspy.mobile.data.IntentRecognitionOptions
import org.rhasspy.mobile.middleware.IServiceMiddleware
import org.rhasspy.mobile.services.mqtt.MqttService
import org.rhasspy.mobile.services.rhasspyactions.RhasspyActionsService
import org.rhasspy.mobile.services.rhasspyactions.RhasspyActionsServiceParams

class IntentRecognitionConfigurationTest : IConfigurationTest() {

    public fun runTest(text: String) {
        testScope.launch {
            //await for mqtt
            if (get<RhasspyActionsServiceParams>().intentRecognitionOption == IntentRecognitionOptions.RemoteMQTT) {
                get<MqttService>()
                    .isHasStarted
                    .map { it }
                    .distinctUntilChanged()
                    .first { it }
            }

            val middleware = get<IServiceMiddleware>()
            get<RhasspyActionsService>().recognizeIntent(middleware.sessionId, text)
        }
    }

    override fun onClose() {
        //TODO("Not yet implemented")
    }
}