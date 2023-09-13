package org.rhasspy.mobile.ui.configuration.audioinput

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import org.rhasspy.mobile.ui.content.elements.RadioButtonsEnumSelectionList
import org.rhasspy.mobile.ui.content.elements.Text
import org.rhasspy.mobile.ui.content.list.ListElement
import org.rhasspy.mobile.ui.main.ConfigurationScreenItemContent
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.viewmodel.configuration.audioinput.audioinputformat.AudioInputFormatConfigurationUiEvent
import org.rhasspy.mobile.viewmodel.configuration.audioinput.audioinputformat.AudioInputFormatConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.audioinput.audioinputformat.AudioInputFormatConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.audioinput.audioinputformat.AudioInputFormatConfigurationViewState.AudioInputFormatConfigurationData
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.AudioInputDomainScreenDestination.AudioInputFormatScreen

/**
 * Content to configure speech to text
 * Drop Down of state
 * HTTP Endpoint
 */
@Composable
fun AudioInputFormatConfigurationScreen(viewModel: AudioInputFormatConfigurationViewModel) {

    val configurationEditViewState by viewModel.configurationViewState.collectAsState()

    ConfigurationScreenItemContent(
        modifier = Modifier.testTag(AudioInputFormatScreen),
        screenViewModel = viewModel,
        title = MR.strings.speechToText.stable, //TODO
        viewState = configurationEditViewState,
        onEvent = viewModel::onEvent
    ) {

        val viewState by viewModel.viewState.collectAsState()

        AudioInputFormatEditContent(
            editData = viewState.editData,
            onEvent = viewModel::onEvent
        )

    }

}

@Composable
private fun AudioInputFormatEditContent(
    editData: AudioInputFormatConfigurationData,
    onEvent: (AudioInputFormatConfigurationUiEvent) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        Card(
            modifier = Modifier.padding(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            ListElement {
                Text(resource = MR.strings.channel.stable)
            }

            RadioButtonsEnumSelectionList(
                modifier = Modifier.testTag(TestTag.AudioInputChannelType),
                selected = editData.audioInputChannel,
                onSelect = { onEvent(SelectInputFormatChannelType(it)) },
                combinedTestTag = TestTag.AudioInputChannelType,
                values = editData.audioFormatChannelTypes
            )
        }

        Card(
            modifier = Modifier.padding(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            ListElement {
                Text(resource = MR.strings.encoding.stable)
            }

            RadioButtonsEnumSelectionList(
                modifier = Modifier.testTag(TestTag.AudioInputEncodingType),
                selected = editData.audioInputEncoding,
                enabled = true, //TODO
                onSelect = { onEvent(SelectInputFormatEncodingType(it)) },
                combinedTestTag = TestTag.AudioInputEncodingType,
                values = editData.audioFormatEncodingTypes
            )
        }

        Card(
            modifier = Modifier.padding(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            ListElement {
                Text(resource = MR.strings.sampleRate.stable)
            }

            RadioButtonsEnumSelectionList(
                modifier = Modifier.testTag(TestTag.AudioInputSampleRateType),
                selected = editData.audioInputSampleRate,
                onSelect = { onEvent(SelectInputFormatSampleRateType(it)) },
                combinedTestTag = TestTag.AudioInputSampleRateType,
                values = editData.audioFormatSampleRateTypes
            )
        }

    }

}