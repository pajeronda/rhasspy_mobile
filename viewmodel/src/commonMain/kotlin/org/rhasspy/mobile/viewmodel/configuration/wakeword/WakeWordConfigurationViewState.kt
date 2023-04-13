package org.rhasspy.mobile.viewmodel.configuration.wakeword

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.toImmutableList
import org.rhasspy.mobile.data.porcupine.PorcupineCustomKeyword
import org.rhasspy.mobile.data.porcupine.PorcupineDefaultKeyword
import org.rhasspy.mobile.data.service.option.PorcupineLanguageOption
import org.rhasspy.mobile.data.service.option.WakeWordOption
import org.rhasspy.mobile.logic.settings.ConfigurationSetting
import org.rhasspy.mobile.platformspecific.toImmutableList
import org.rhasspy.mobile.platformspecific.toIntOrZero
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationEditViewState
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationViewModel.PorcupineCustomKeywordUi

@Stable
data class WakeWordConfigurationViewState(
    val wakeWordOptions: ImmutableList<WakeWordOption> = WakeWordOption.values().toImmutableList(),
    val wakeWordOption: WakeWordOption= ConfigurationSetting.wakeWordOption.value,
    val wakeWordPorcupineViewState: PorcupineViewState = PorcupineViewState(),
    val wakeWordUdpViewState: UdpViewState = UdpViewState()
): IConfigurationEditViewState {

    override val hasUnsavedChanges: Boolean
        get() = !(wakeWordOption == ConfigurationSetting.wakeWordOption.value &&
                wakeWordPorcupineViewState.hasUnsavedChanges.not() &&
                wakeWordUdpViewState.hasUnsavedChanges.not())

    override val isTestingEnabled: Boolean get() = wakeWordOption != WakeWordOption.Disabled

    @Stable
    data class PorcupineViewState(
        val accessToken: String= ConfigurationSetting.wakeWordPorcupineAccessToken.value,
        val defaultOptions: ImmutableList<PorcupineDefaultKeyword> = ConfigurationSetting.wakeWordPorcupineKeywordDefaultOptions.value,
        val customOptions: ImmutableSet<PorcupineCustomKeyword> = ConfigurationSetting.wakeWordPorcupineKeywordCustomOptions.value,
        val languageOptions: ImmutableList<PorcupineLanguageOption> = PorcupineLanguageOption.values().toImmutableList(),
        val porcupineLanguage: PorcupineLanguageOption= ConfigurationSetting.wakeWordPorcupineLanguage.value
    ) {
        val hasUnsavedChanges: Boolean
            get() = !(accessToken == ConfigurationSetting.wakeWordPorcupineAccessToken.value &&
                    defaultOptions == ConfigurationSetting.wakeWordPorcupineKeywordDefaultOptions.value &&
                    customOptions == ConfigurationSetting.wakeWordPorcupineKeywordCustomOptions.value &&
                    porcupineLanguage == ConfigurationSetting.wakeWordPorcupineLanguage.value)

        val customOptionsUi: ImmutableList<PorcupineCustomKeywordUi> get() = customOptions.map { PorcupineCustomKeywordUi(it) }.toImmutableList()
        val keywordCount: Int get() = defaultOptions.count { it.isEnabled } + customOptions.count { it.isEnabled }
    }

    @Stable
    data class UdpViewState(
        val outputHost: String= ConfigurationSetting.wakeWordUdpOutputHost.value,
        val outputPortText: String = ConfigurationSetting.wakeWordUdpOutputPort.value.toString()
    ) {
        val hasUnsavedChanges: Boolean
            get() = !(outputHost == ConfigurationSetting.wakeWordUdpOutputHost.value &&
                    outputPort == ConfigurationSetting.wakeWordUdpOutputPort.value)

        val outputPort: Int get() = outputPortText.toIntOrZero()
    }

}