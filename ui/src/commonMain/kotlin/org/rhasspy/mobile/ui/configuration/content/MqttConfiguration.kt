package org.rhasspy.mobile.ui.configuration.content

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.*
import org.rhasspy.mobile.ui.configuration.ConfigurationScreenConfig
import org.rhasspy.mobile.ui.configuration.ConfigurationScreenItemContent
import org.rhasspy.mobile.ui.content.elements.Icon
import org.rhasspy.mobile.ui.content.elements.Text
import org.rhasspy.mobile.ui.content.elements.translate
import org.rhasspy.mobile.ui.content.list.*
import org.rhasspy.mobile.viewmodel.configuration.edit.mqtt.MqttConfigurationUiEvent
import org.rhasspy.mobile.viewmodel.configuration.edit.mqtt.MqttConfigurationUiEvent.Action.OpenMqttSSLWiki
import org.rhasspy.mobile.viewmodel.configuration.edit.mqtt.MqttConfigurationUiEvent.Action.SelectSSLCertificate
import org.rhasspy.mobile.viewmodel.configuration.edit.mqtt.MqttConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.edit.mqtt.MqttConfigurationUiEvent.Consumed.ShowSnackBar
import org.rhasspy.mobile.viewmodel.configuration.edit.mqtt.MqttConfigurationEditViewModel
import org.rhasspy.mobile.viewmodel.navigation.destinations.ConfigurationScreenNavigationDestination.MqttConfigurationScreen

/**
 * mqtt configuration content
 * Enable/Disable switch
 * when enabled then
 * connection settings (host, port)
 * option to test connection
 * ssl settings
 * connection timeout settings
 */
@Composable
fun MqttConfigurationContent() {
    val viewModel: MqttConfigurationEditViewModel = LocalViewModelFactory.current.getViewModel()

    Screen(viewModel) {
        val viewState by viewModel.viewState.collectAsState()
        val screen by viewModel.screen.collectAsState()
        val contentViewState by viewState.editViewState.collectAsState()

        val snackBarHostState = LocalSnackBarHostState.current
        val snackBarText = contentViewState.snackBarText?.let { translate(it) }

        LaunchedEffect(snackBarText) {
            snackBarText?.also {
                snackBarHostState.showSnackbar(message = it)
                viewModel.onEvent(ShowSnackBar)
            }
        }
        ConfigurationScreenItemContent(
            modifier = Modifier.testTag(MqttConfigurationScreen),
            screenType = screen.destinationType,
            config = ConfigurationScreenConfig(MR.strings.mqtt.stable),
            viewState = viewState,
            onAction = viewModel::onAction
        ) {

            item {
                //toggle to turn mqtt enabled on or off
                SwitchListItem(
                    text = MR.strings.externalMQTT.stable,
                    modifier = Modifier.testTag(TestTag.MqttSwitch),
                    isChecked = contentViewState.isMqttEnabled,
                    onCheckedChange = { viewModel.onEvent(SetMqttEnabled(it)) }
                )
            }

            item {
                //visibility of mqtt settings
                AnimatedVisibility(
                    enter = expandVertically(),
                    exit = shrinkVertically(),
                    visible = contentViewState.isMqttEnabled
                ) {

                    Column {

                        MqttConnectionSettings(
                            mqttHost = contentViewState.mqttHost,
                            mqttPortText = contentViewState.mqttPortText,
                            mqttUserName = contentViewState.mqttUserName,
                            mqttPassword = contentViewState.mqttPassword,
                            onAction = viewModel::onEvent
                        )

                        MqttSSL(
                            isMqttSSLEnabled = contentViewState.isMqttSSLEnabled,
                            mqttKeyStoreFileName = contentViewState.mqttKeyStoreFileName,
                            onAction = viewModel::onEvent
                        )

                        MqttConnectionTiming(
                            mqttConnectionTimeoutText = contentViewState.mqttConnectionTimeoutText,
                            mqttKeepAliveIntervalText = contentViewState.mqttKeepAliveIntervalText,
                            mqttRetryIntervalText = contentViewState.mqttRetryIntervalText,
                            onAction = viewModel::onEvent
                        )

                    }

                }
            }

        }
    }

}

/**
 * connection settings for mqtt
 * text fields for
 * host, port, username, password
 */
@Composable
private fun MqttConnectionSettings(
    mqttHost: String,
    mqttPortText: String,
    mqttUserName: String,
    mqttPassword: String,
    onAction: (MqttConfigurationUiEvent) -> Unit
) {

    //host
    TextFieldListItem(
        label = MR.strings.host.stable,
        modifier = Modifier.testTag(TestTag.Host),
        value = mqttHost,
        onValueChange = { onAction(UpdateMqttHost(it)) },
        isLastItem = false
    )

    //port
    TextFieldListItem(
        label = MR.strings.port.stable,
        modifier = Modifier.testTag(TestTag.Port),
        value = mqttPortText,
        onValueChange = { onAction(UpdateMqttPort(it)) },
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        isLastItem = false
    )

    //username
    TextFieldListItem(
        label = MR.strings.userName.stable,
        modifier = Modifier.testTag(TestTag.UserName),
        value = mqttUserName,
        onValueChange = { onAction(UpdateMqttUserName(it)) },
        isLastItem = false
    )

    //password
    TextFieldListItemVisibility(
        label = MR.strings.password.stable,
        modifier = Modifier.testTag(TestTag.Password),
        value = mqttPassword,
        onValueChange = { onAction(UpdateMqttPassword(it)) }
    )
}

/**
 * switch to enable mqtt ssl
 * button to select certificate
 */
@Composable
private fun MqttSSL(
    isMqttSSLEnabled: Boolean,
    mqttKeyStoreFileName: String?,
    onAction: (MqttConfigurationUiEvent) -> Unit
) {

    SwitchListItem(
        text = MR.strings.enableSSL.stable,
        modifier = Modifier.testTag(TestTag.SSLSwitch),
        isChecked = isMqttSSLEnabled,
        onCheckedChange = { onAction(SetMqttSSLEnabled(it)) }
    )

    AnimatedVisibility(
        enter = expandVertically(),
        exit = shrinkVertically(),
        visible = isMqttSSLEnabled
    ) {

        Column {

            ListElement(
                modifier = Modifier
                    .testTag(TestTag.MQTTSSLWiki)
                    .clickable(onClick = { onAction(OpenMqttSSLWiki) }),
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Link,
                        contentDescription = MR.strings.sslWiki.stable
                    )
                },
                text = { Text(MR.strings.sslWiki.stable) },
                secondaryText = { Text(MR.strings.sslWikiInfo.stable) }
            )

            FilledTonalButtonListItem(
                text = MR.strings.chooseCertificate.stable,
                modifier = Modifier.testTag(TestTag.CertificateButton),
                onClick = { onAction(SelectSSLCertificate) }
            )

            val isKeyStoreFileTextVisible by remember { derivedStateOf { mqttKeyStoreFileName != null } }

            AnimatedVisibility(
                enter = expandVertically(),
                exit = shrinkVertically(),
                visible = isKeyStoreFileTextVisible
            ) {

                val keyStoreFileText by remember { derivedStateOf { mqttKeyStoreFileName ?: "" } }

                InformationListElement(
                    text = translate(resource = MR.strings.currentlySelectedCertificate.stable, keyStoreFileText)
                )
            }

        }
    }

}

/**
 * Time settings for mqtt connection
 * timeout, keepAliveInterval, retryInterval
 */
@Composable
private fun MqttConnectionTiming(
    mqttConnectionTimeoutText: String,
    mqttKeepAliveIntervalText: String,
    mqttRetryIntervalText: String,
    onAction: (MqttConfigurationUiEvent) -> Unit
) {

    TextFieldListItem(
        label = MR.strings.connectionTimeout.stable,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        modifier = Modifier.testTag(TestTag.ConnectionTimeout),
        value = mqttConnectionTimeoutText,
        onValueChange = { onAction(UpdateMqttConnectionTimeout(it)) },
        isLastItem = false
    )

    TextFieldListItem(
        label = MR.strings.keepAliveInterval.stable,
        modifier = Modifier.testTag(TestTag.KeepAliveInterval),
        value = mqttKeepAliveIntervalText,
        onValueChange = { onAction(UpdateMqttKeepAliveInterval(it)) },
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        isLastItem = false
    )

    TextFieldListItem(
        label = MR.strings.retryInterval.stable,
        modifier = Modifier.testTag(TestTag.RetryInterval),
        value = mqttRetryIntervalText,
        onValueChange = { onAction(UpdateMqttRetryInterval(it)) },
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
    )

}