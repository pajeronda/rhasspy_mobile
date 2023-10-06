package org.rhasspy.mobile.ui.configuration.domains.mic

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.content.ScreenContent
import org.rhasspy.mobile.ui.content.elements.RadioButtonsEnumSelectionList
import org.rhasspy.mobile.ui.content.elements.Text
import org.rhasspy.mobile.ui.content.list.ListElement
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.ui.theme.TonalElevationLevel2
import org.rhasspy.mobile.ui.theme.TonalElevationLevel3
import org.rhasspy.mobile.viewmodel.configuration.domains.mic.audioinputformat.AudioRecorderFormatConfigurationUiEvent
import org.rhasspy.mobile.viewmodel.configuration.domains.mic.audioinputformat.AudioRecorderFormatConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.domains.mic.audioinputformat.AudioRecorderFormatConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.domains.mic.audioinputformat.AudioRecorderFormatConfigurationViewState.AudioRecorderFormatConfigurationData
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.AudioInputDomainScreenDestination.AudioInputFormatScreen

/**
 * Content to configure speech to text
 * Drop Down of state
 * HTTP Endpoint
 */
@Composable
fun AudioRecorderFormatConfigurationScreen(viewModel: AudioRecorderFormatConfigurationViewModel) {

    ScreenContent(
        modifier = Modifier.testTag(AudioInputFormatScreen),
        title = MR.strings.audioRecorderFormat.stable,
        viewModel = viewModel,
        tonalElevation = TonalElevationLevel2,
    ) {

        val viewState by viewModel.viewState.collectAsState()

        AudioRecorderFormatEditContent(
            editData = viewState.editData,
            onEvent = viewModel::onEvent
        )

    }

}

@Composable
private fun AudioRecorderFormatEditContent(
    editData: AudioRecorderFormatConfigurationData,
    onEvent: (AudioRecorderFormatConfigurationUiEvent) -> Unit
) {

    Column(
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {

        Card(
            modifier = Modifier.padding(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = TonalElevationLevel3)
        ) {
            ListElement {
                Text(resource = MR.strings.channel.stable)
            }

            RadioButtonsEnumSelectionList(
                modifier = Modifier.testTag(TestTag.AudioInputChannelType),
                selected = editData.audioInputChannel,
                onSelect = { onEvent(SelectRecorderFormatChannelType(it)) },
                combinedTestTag = TestTag.AudioInputChannelType,
                values = editData.audioFormatChannelTypes
            )
        }

        Card(
            modifier = Modifier.padding(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = TonalElevationLevel3)
        ) {
            ListElement {
                Text(resource = MR.strings.encoding.stable)
            }

            RadioButtonsEnumSelectionList(
                modifier = Modifier.testTag(TestTag.AudioInputEncodingType),
                selected = editData.audioInputEncoding,
                onSelect = { onEvent(SelectRecorderFormatEncodingType(it)) },
                combinedTestTag = TestTag.AudioInputEncodingType,
                values = editData.audioFormatEncodingTypes
            )
        }

        Card(
            modifier = Modifier.padding(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = TonalElevationLevel3)
        ) {
            ListElement {
                Text(resource = MR.strings.sampleRate.stable)
            }

            RadioButtonsEnumSelectionList(
                modifier = Modifier.testTag(TestTag.AudioInputSampleRateType),
                selected = editData.audioInputSampleRate,
                onSelect = { onEvent(SelectRecorderFormatSampleRateType(it)) },
                combinedTestTag = TestTag.AudioInputSampleRateType,
                values = editData.audioFormatSampleRateTypes
            )
        }

    }

}