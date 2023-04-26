package org.rhasspy.mobile.logic.services.homeassistant

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.settings.ConfigurationSetting

class HomeAssistantServiceParamsCreator {

    private val updaterScope = CoroutineScope(Dispatchers.Default)
    private val paramsFlow = MutableStateFlow(getParams())

    operator fun invoke(): StateFlow<HomeAssistantServiceParams> {

        updaterScope.launch {
            combineStateFlow(
                ConfigurationSetting.siteId.data,
                ConfigurationSetting.intentHandlingHomeAssistantOption.data
            ).collect {
                paramsFlow.value = getParams()
            }
        }

        return paramsFlow
    }

    private fun getParams(): HomeAssistantServiceParams {
        return HomeAssistantServiceParams(
            siteId = ConfigurationSetting.siteId.value,
            intentHandlingHomeAssistantOption = ConfigurationSetting.intentHandlingHomeAssistantOption.value
        )
    }

}