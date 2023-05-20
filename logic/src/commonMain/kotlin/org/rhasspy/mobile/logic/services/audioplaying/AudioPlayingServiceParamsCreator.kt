package org.rhasspy.mobile.logic.services.audioplaying

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.settings.ConfigurationSetting

class AudioPlayingServiceParamsCreator {

    private val updaterScope = CoroutineScope(Dispatchers.IO)
    private val paramsFlow = MutableStateFlow(getParams())

    operator fun invoke(): StateFlow<AudioPlayingServiceParams> {

        updaterScope.launch {
            combineStateFlow(
                ConfigurationSetting.audioPlayingOption.data
            ).collect {
                paramsFlow.value = getParams()
            }
        }

        return paramsFlow
    }

    private fun getParams(): AudioPlayingServiceParams {
        return AudioPlayingServiceParams(
            audioPlayingOption = ConfigurationSetting.audioPlayingOption.value
        )
    }

}