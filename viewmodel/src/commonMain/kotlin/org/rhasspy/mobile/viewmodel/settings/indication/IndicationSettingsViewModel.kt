package org.rhasspy.mobile.viewmodel.settings.indication

import androidx.compose.runtime.Stable
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.viewmodel.screen.ScreenViewModel
import org.rhasspy.mobile.viewmodel.settings.indication.IndicationSettingsUiEvent.Change
import org.rhasspy.mobile.viewmodel.settings.indication.IndicationSettingsUiEvent.Change.SetWakeWordDetectionTurnOnDisplay
import org.rhasspy.mobile.viewmodel.settings.indication.IndicationSettingsUiEvent.Change.SetWakeWordLightIndicationEnabled

@Stable
class IndicationSettingsViewModel(
    viewStateCreator: IndicationSettingsViewStateCreator
) : ScreenViewModel() {

    val viewState = viewStateCreator()

    fun onEvent(event: IndicationSettingsUiEvent) {
        when (event) {
            is Change -> onChange(event)
        }
    }

    private fun onChange(change: Change) {
        when (change) {
            is SetWakeWordDetectionTurnOnDisplay ->
                AppSetting.isWakeWordDetectionTurnOnDisplayEnabled.value = change.enabled

            is SetWakeWordLightIndicationEnabled -> {
                requireOverlayPermission {
                    AppSetting.isWakeWordLightIndicationEnabled.value = change.enabled
                }
            }
        }
    }

}