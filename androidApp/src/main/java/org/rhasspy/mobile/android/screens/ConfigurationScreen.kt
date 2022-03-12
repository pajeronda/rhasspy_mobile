package org.rhasspy.mobile.android.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.permissions.requestMicrophonePermission
import org.rhasspy.mobile.android.utils.*
import org.rhasspy.mobile.data.*
import org.rhasspy.mobile.nativeutils.MicrophonePermission
import org.rhasspy.mobile.settings.ConfigurationSettings
import java.math.RoundingMode

@Composable
fun ConfigurationScreen(snackbarHostState: SnackbarHostState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        SiteId()
        Divider()
        HttpSSL()
        Divider()
        Mqtt()
        Divider()
        AudioRecording()
        Divider()
        WakeWord(snackbarHostState)
        Divider()
        SpeechToText()
        Divider()
        IntentRecognition()
        Divider()
        TextToSpeech()
        Divider()
        AudioPlaying()
        Divider()
        DialogueManagement()
        Divider()
        IntentHandling()
        Divider()
    }
}

@Composable
fun SiteId() {

    TextFieldListItem(
        value = ConfigurationSettings.siteId.observeCurrent(),
        onValueChange = { ConfigurationSettings.siteId.unsavedData = it },
        label = MR.strings.siteId,
        paddingValues = PaddingValues(top = 4.dp, bottom = 16.dp)
    )
}

@Composable
fun HttpSSL() {

    val isHttpSSLValue = ConfigurationSettings.isHttpSSL.observeCurrent()

    ExpandableListItem(
        text = MR.strings.httpSSL,
        secondaryText = isHttpSSLValue.toText()
    ) {

        SwitchListItem(
            text = MR.strings.enableSSL,
            isChecked = isHttpSSLValue,
            onCheckedChange = { ConfigurationSettings.isHttpSSL.unsavedData = it })

        AnimatedVisibility(
            enter = expandVertically(),
            exit = shrinkVertically(),
            visible = isHttpSSLValue
        ) {
            OutlineButtonListItem(
                text = MR.strings.chooseCertificate,
                onClick = { })
        }

    }
}

@Composable
fun Mqtt() {
    ExpandableListItem(
        text = MR.strings.mqtt,
        secondaryText = MR.strings.notConnected
    ) {

        TextFieldListItem(
            label = MR.strings.host,
            value = ConfigurationSettings.mqttHost.observeCurrent(),
            onValueChange = { ConfigurationSettings.mqttHost.unsavedData = it },
        )

        TextFieldListItem(
            label = MR.strings.port,
            value = ConfigurationSettings.mqttPort.observeCurrent(),
            onValueChange = { ConfigurationSettings.mqttPort.unsavedData = it },
        )

        TextFieldListItem(
            value = ConfigurationSettings.mqttUserName.observeCurrent(),
            onValueChange = { ConfigurationSettings.mqttUserName.unsavedData = it },
            label = MR.strings.userName
        )

        var isShowPassword by rememberSaveable { mutableStateOf(false) }

        TextFieldListItem(
            value = ConfigurationSettings.mqttPassword.observeCurrent(),
            onValueChange = { ConfigurationSettings.mqttPassword.unsavedData = it },
            label = MR.strings.password,
            visualTransformation = if (isShowPassword) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { isShowPassword = !isShowPassword }) {
                    Icon(
                        if (isShowPassword) {
                            Icons.Filled.Visibility
                        } else {
                            Icons.Filled.VisibilityOff
                        },
                        contentDescription = MR.strings.visibility,
                    )
                }
            },
        )

        val isMqttSSL = ConfigurationSettings.isMqttSSL.observeCurrent()

        SwitchListItem(
            text = MR.strings.enableSSL,
            isChecked = isMqttSSL,
            onCheckedChange = { ConfigurationSettings.isMqttSSL.unsavedData = it })

        AnimatedVisibility(
            enter = expandVertically(),
            exit = shrinkVertically(),
            visible = isMqttSSL
        ) {
            OutlineButtonListItem(
                text = MR.strings.chooseCertificate,
                onClick = { })
        }

        Box(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth()
                .wrapContentSize(Alignment.Center)
        ) {
            Button(onClick = { }) {
                Icon(Icons.Filled.Link, MR.strings.checkConnection)
                Spacer(modifier = Modifier.width(8.dp))
                Text(MR.strings.checkConnection)
            }
        }
    }
}

@Composable
fun AudioRecording() {

    val isUDPOutput = ConfigurationSettings.isUDPOutput.observeCurrent()

    ExpandableListItem(
        text = MR.strings.audioRecording,
        secondaryText =
        if (isUDPOutput)
            MR.strings.udpAudioOutputOn
        else
            MR.strings.udpAudioOutputOff
    ) {

        SwitchListItem(
            text = MR.strings.udpAudioOutput,
            secondaryText = MR.strings.udpAudioOutputDetail,
            isChecked = isUDPOutput,
            onCheckedChange = { ConfigurationSettings.isUDPOutput.unsavedData = it })

        AnimatedVisibility(
            enter = expandVertically(),
            exit = shrinkVertically(),
            visible = isUDPOutput
        ) {

            Column {
                TextFieldListItem(
                    label = MR.strings.host,
                    value = ConfigurationSettings.udpOutputHost.observeCurrent(),
                    onValueChange = { ConfigurationSettings.udpOutputHost.unsavedData = it },
                )

                TextFieldListItem(
                    label = MR.strings.port,
                    value = ConfigurationSettings.udpOutputPort.observeCurrent(),
                    onValueChange = { ConfigurationSettings.udpOutputPort.unsavedData = it },
                )
            }
        }

    }
}

@Composable
fun WakeWord(snackbarHostState: SnackbarHostState) {

    val wakeWordValueOption = ConfigurationSettings.wakeWordOption.observeCurrent()

    ExpandableListItem(
        text = MR.strings.wakeWord,
        secondaryText = wakeWordValueOption.text
    ) {

        val requestMicrophonePermission = requestMicrophonePermission(snackbarHostState, MR.strings.microphonePermissionInfoWakeWord) {
            if (it) {
                ConfigurationSettings.wakeWordOption.unsavedData = WakeWordOption.Porcupine
            }
        }

        DropDownEnumListItem(
            selected = wakeWordValueOption,
            onSelect = {
                if (it == WakeWordOption.Porcupine && !MicrophonePermission.granted.value) {
                    requestMicrophonePermission.invoke()
                } else {
                    ConfigurationSettings.wakeWordOption.unsavedData = it
                }
            })
        { WakeWordOption.values() }

        AnimatedVisibility(
            enter = expandVertically(),
            exit = shrinkVertically(),
            visible = ConfigurationSettings.wakeWordOption.observeCurrent() == WakeWordOption.Porcupine
        ) {

            Column {
                TextFieldListItem(
                    value = ConfigurationSettings.wakeWordAccessToken.observeCurrent(),
                    onValueChange = { ConfigurationSettings.wakeWordAccessToken.unsavedData = it },
                    label = MR.strings.porcupineAccessKey
                )

                val context = LocalContext.current

                OutlineButtonListItem(
                    text = MR.strings.openPicoVoiceConsole,
                    onClick = {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://console.picovoice.ai/access_key")))
                    })

                //filled with correct values later
                DropDownEnumListItem(
                    selected = ConfigurationSettings.wakeWordKeywordOption.observeCurrent(),
                    onSelect = { ConfigurationSettings.wakeWordKeywordOption.unsavedData = it })
                { WakeWordKeywordOption.values() }

                SliderListItem(
                    text = MR.strings.sensitivity,
                    value = ConfigurationSettings.wakeWordKeywordSensitivity.observeCurrent(),
                    onValueChange = {
                        ConfigurationSettings.wakeWordKeywordSensitivity.unsavedData = it.toBigDecimal().setScale(2, RoundingMode.HALF_DOWN).toFloat()
                    })
            }
        }
    }
}

@Composable
fun SpeechToText() {

    val speechToTextOption = ConfigurationSettings.speechToTextOption.observeCurrent()

    ExpandableListItem(
        text = MR.strings.speechToText,
        secondaryText = speechToTextOption.text
    ) {
        DropDownEnumListItem(
            selected = speechToTextOption,
            onSelect = { ConfigurationSettings.speechToTextOption.unsavedData = it })
        { SpeechToTextOptions.values() }

        AnimatedVisibility(
            enter = expandVertically(),
            exit = shrinkVertically(),
            visible = speechToTextOption == SpeechToTextOptions.RemoteHTTP
        ) {

            TextFieldListItem(
                value = ConfigurationSettings.speechToTextHttpEndpoint.observeCurrent(),
                onValueChange = { ConfigurationSettings.speechToTextHttpEndpoint.unsavedData = it },
                label = MR.strings.speechToTextURL
            )

        }
    }
}

@Composable
fun IntentRecognition() {

    val intentRecognitionOption = ConfigurationSettings.intentRecognitionOption.observeCurrent()

    ExpandableListItem(
        text = MR.strings.intentRecognition,
        secondaryText = intentRecognitionOption.text
    ) {
        DropDownEnumListItem(
            selected = intentRecognitionOption,
            onSelect = { ConfigurationSettings.intentRecognitionOption.unsavedData = it })
        { IntentRecognitionOptions.values() }

        AnimatedVisibility(
            enter = expandVertically(),
            exit = shrinkVertically(),
            visible = intentRecognitionOption == IntentRecognitionOptions.RemoteHTTP
        ) {

            TextFieldListItem(
                value = ConfigurationSettings.intentRecognitionEndpoint.observeCurrent(),
                onValueChange = { ConfigurationSettings.intentRecognitionEndpoint.unsavedData = it },
                label = MR.strings.rhasspyTextToIntentURL
            )

        }
    }
}

@Composable
fun TextToSpeech() {

    val textToSpeechOption = ConfigurationSettings.textToSpeechOption.observeCurrent()

    ExpandableListItem(
        text = MR.strings.textToSpeech,
        secondaryText = textToSpeechOption.text
    ) {
        DropDownEnumListItem(
            selected = textToSpeechOption,
            onSelect = { ConfigurationSettings.textToSpeechOption.unsavedData = it })
        { TextToSpeechOptions.values() }

        AnimatedVisibility(
            enter = expandVertically(),
            exit = shrinkVertically(),
            visible = textToSpeechOption == TextToSpeechOptions.RemoteHTTP
        ) {

            TextFieldListItem(
                value = ConfigurationSettings.textToSpeechEndpoint.observeCurrent(),
                onValueChange = { ConfigurationSettings.textToSpeechEndpoint.unsavedData = it },
                label = MR.strings.rhasspyTextToSpeechURL
            )

        }
    }
}

@Composable
fun AudioPlaying() {

    val audioPlayingOption = ConfigurationSettings.audioPlayingOption.observeCurrent()

    ExpandableListItem(
        text = MR.strings.audioPlaying,
        secondaryText = audioPlayingOption.text
    ) {
        DropDownEnumListItem(
            selected = audioPlayingOption,
            onSelect = { ConfigurationSettings.audioPlayingOption.unsavedData = it })
        { AudioPlayingOptions.values() }

        AnimatedVisibility(
            enter = expandVertically(),
            exit = shrinkVertically(),
            visible = audioPlayingOption == AudioPlayingOptions.RemoteHTTP
        ) {

            TextFieldListItem(
                value = ConfigurationSettings.audioPlayingEndpoint.observeCurrent(),
                onValueChange = { ConfigurationSettings.audioPlayingEndpoint.unsavedData = it },
                label = MR.strings.audioOutputURL
            )
        }
    }
}

@Composable
fun DialogueManagement() {

    val dialogueManagementOption = ConfigurationSettings.dialogueManagementOption.observeCurrent()

    ExpandableListItem(
        text = MR.strings.dialogueManagement,
        secondaryText = dialogueManagementOption.text
    ) {
        DropDownEnumListItem(
            selected = dialogueManagementOption,
            onSelect = { ConfigurationSettings.dialogueManagementOption.unsavedData = it })
        { DialogueManagementOptions.values() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntentHandling() {

    val intentHandlingOption = ConfigurationSettings.intentHandlingOption.observeCurrent()

    ExpandableListItem(
        text = MR.strings.intentHandling,
        secondaryText = intentHandlingOption.text
    ) {
        DropDownEnumListItem(
            selected = intentHandlingOption,
            onSelect = { ConfigurationSettings.intentHandlingOption.unsavedData = it })
        { IntentHandlingOptions.values() }

        AnimatedVisibility(
            enter = expandVertically(),
            exit = shrinkVertically(),
            visible = intentHandlingOption == IntentHandlingOptions.RemoteHTTP
        ) {

            TextFieldListItem(
                value = ConfigurationSettings.intentHandlingEndpoint.observeCurrent(),
                onValueChange = { ConfigurationSettings.intentHandlingEndpoint.unsavedData = it },
                label = MR.strings.remoteURL
            )
        }

        AnimatedVisibility(
            enter = expandVertically(),
            exit = shrinkVertically(),
            visible = intentHandlingOption == IntentHandlingOptions.HomeAssistant
        ) {
            Column {

                TextFieldListItem(
                    value = ConfigurationSettings.intentHandlingHassUrl.observeCurrent(),
                    onValueChange = { ConfigurationSettings.intentHandlingHassUrl.unsavedData = it },
                    label = MR.strings.hassURL
                )

                TextFieldListItem(
                    value = ConfigurationSettings.intentHandlingHassAccessToken.observeCurrent(),
                    onValueChange = { ConfigurationSettings.intentHandlingHassAccessToken.unsavedData = it },
                    label = MR.strings.accessToken
                )

                val isIntentHandlingHassEvent = ConfigurationSettings.isIntentHandlingHassEvent.observeCurrent()

                RadioButtonListItem(
                    text = MR.strings.homeAssistantEvents,
                    isChecked = isIntentHandlingHassEvent,
                    onClick = {
                        ConfigurationSettings.isIntentHandlingHassEvent.unsavedData = true
                    })


                RadioButtonListItem(
                    text = MR.strings.homeAssistantIntents,
                    isChecked = !isIntentHandlingHassEvent,
                    onClick = {
                        ConfigurationSettings.isIntentHandlingHassEvent.unsavedData = false
                    })
            }
        }
    }
}