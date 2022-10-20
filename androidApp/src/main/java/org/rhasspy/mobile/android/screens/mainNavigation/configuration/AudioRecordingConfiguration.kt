package org.rhasspy.mobile.android.screens.mainNavigation.configuration

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.utils.SwitchListItem
import org.rhasspy.mobile.android.utils.TextFieldListItem
import org.rhasspy.mobile.viewModels.configuration.AudioRecordingConfigurationViewModel


/**
 * Content to configure audio recording
 * Udp audio output on/off
 * also udp host and port
 */
@Preview
@Composable
fun AudioRecordingConfigurationContent(viewModel: AudioRecordingConfigurationViewModel = viewModel()) {

    Column(
        modifier = Modifier
            .padding(bottom = 8.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        //switch to enable udp output
        SwitchListItem(
            text = MR.strings.udpAudioOutput,
            secondaryText = MR.strings.udpAudioOutputDetail,
            isChecked = viewModel.isUdpOutputEnabled.collectAsState().value,
            onCheckedChange = viewModel::toggleUdpOutputEnabled
        )

        //visibility of udp host and port settings
        AnimatedVisibility(
            enter = expandVertically(),
            exit = shrinkVertically(),
            visible = viewModel.isOutputSettingsVisible.collectAsState().value
        ) {

            //udp host and port
            UdpSettings(viewModel)

        }

    }

}

/**
 *  udp host and port
 */
@Composable
private fun UdpSettings(viewModel: AudioRecordingConfigurationViewModel) {

    Column {

        //udp host
        TextFieldListItem(
            label = MR.strings.host,
            value = viewModel.udpOutputHost.collectAsState().value,
            onValueChange = viewModel::changeUdpOutputHost
        )

        //udp port
        TextFieldListItem(
            label = MR.strings.port,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            value = viewModel.udpOutputPort.collectAsState().value,
            onValueChange = viewModel::changeUdpOutputPort
        )

    }

}