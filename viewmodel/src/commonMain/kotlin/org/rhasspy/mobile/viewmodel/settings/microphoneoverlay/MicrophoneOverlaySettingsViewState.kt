package org.rhasspy.mobile.viewmodel.settings.microphoneoverlay

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import org.rhasspy.mobile.data.service.option.MicrophoneOverlaySizeOption
import org.rhasspy.mobile.platformspecific.toImmutableList
import org.rhasspy.mobile.settings.AppSetting

@Stable
data class MicrophoneOverlaySettingsViewState internal constructor(
    val microphoneOverlaySizeOption: MicrophoneOverlaySizeOption = AppSetting.microphoneOverlaySizeOption.value,
    val isMicrophoneOverlayWhileAppEnabled: Boolean = AppSetting.isMicrophoneOverlayWhileAppEnabled.value
) {

    val microphoneOverlaySizeOptions: ImmutableList<MicrophoneOverlaySizeOption> = MicrophoneOverlaySizeOption.values().toImmutableList()

}