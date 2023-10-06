package org.rhasspy.mobile.viewmodel.configuration.domains.mic.audioinputformat

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.domains.mic.audioinputformat.AudioRecorderFormatConfigurationUiEvent.Change
import org.rhasspy.mobile.viewmodel.configuration.domains.mic.audioinputformat.AudioRecorderFormatConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.screen.ScreenViewModel

class AudioRecorderFormatConfigurationViewModel(
    private val mapper: AudioRecorderFormatConfigurationDataMapper,
) : ScreenViewModel() {

    private val _viewState = MutableStateFlow(
        AudioRecorderFormatConfigurationViewState(
            editData = mapper(ConfigurationSetting.micDomainData.value)
        )
    )
    val viewState = _viewState.readOnly

    fun onEvent(event: AudioRecorderFormatConfigurationUiEvent) {
        when (event) {
            is Change -> onChange(event)
        }
    }

    private fun onChange(change: Change) {
        _viewState.update {
            it.copy(editData = with(it.editData) {
                when (change) {
                    is SelectRecorderFormatChannelType    -> copy(audioInputChannel = change.value)
                    is SelectRecorderFormatEncodingType   -> copy(audioInputEncoding = change.value)
                    is SelectRecorderFormatSampleRateType -> copy(audioInputSampleRate = change.value)
                }
            })
        }
        ConfigurationSetting.micDomainData.value = mapper(_viewState.value.editData)
    }

}