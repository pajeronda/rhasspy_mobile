package org.rhasspy.mobile.viewmodel.screens.home

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.rhasspy.mobile.logic.middleware.ServiceMiddleware
import org.rhasspy.mobile.logic.settings.AppSetting
import org.rhasspy.mobile.logic.settings.ConfigurationSetting
import org.rhasspy.mobile.platformspecific.combineStateFlow

class HomeScreenViewStateCreator(
    private val serviceMiddleware: ServiceMiddleware
) {

    private val updaterScope = CoroutineScope(Dispatchers.Default)

    operator fun invoke(): StateFlow<HomeScreenViewState> {
        val viewState = MutableStateFlow(getViewState())

        updaterScope.launch {
            combineStateFlow(
                ConfigurationSetting.wakeWordOption.data,
                serviceMiddleware.isPlayingRecording,
                serviceMiddleware.isPlayingRecordingEnabled,
                AppSetting.isShowLogEnabled.data
            ).onEach {
                viewState.value = getViewState()
            }
        }

        return viewState
    }

    private fun getViewState(): HomeScreenViewState {
        return HomeScreenViewState(
            wakeWordOption = ConfigurationSetting.wakeWordOption.value,
            isPlayingRecording = serviceMiddleware.isPlayingRecording.value,
            isPlayingRecordingEnabled = serviceMiddleware.isPlayingRecordingEnabled.value,
            isShowLogEnabled = AppSetting.isShowLogEnabled.data.value,
        )
    }
}