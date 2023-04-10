package org.rhasspy.mobile.android.configuration.content

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import org.koin.androidx.compose.get
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.configuration.ConfigurationScreenItemContent
import org.rhasspy.mobile.android.configuration.ConfigurationScreenType
import org.rhasspy.mobile.android.content.elements.Icon
import org.rhasspy.mobile.android.content.elements.Text
import org.rhasspy.mobile.android.content.elements.translate
import org.rhasspy.mobile.android.content.list.FilledTonalButtonListItem
import org.rhasspy.mobile.android.content.list.InformationListElement
import org.rhasspy.mobile.android.content.list.ListElement
import org.rhasspy.mobile.android.content.list.SwitchListItem
import org.rhasspy.mobile.android.content.list.TextFieldListItem
import org.rhasspy.mobile.android.content.list.TextFieldListItemVisibility
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.viewmodel.configuration.webserver.WebServerConfigurationViewModel

/**
 * Content to configure text to speech
 * Enable or disable
 * select port
 * select ssl certificate
 */
@Preview
@Composable
fun WebServerConfigurationContent(viewModel: WebServerConfigurationViewModel = get()) {

    ConfigurationScreenItemContent(
        modifier = Modifier.testTag(ConfigurationScreenType.WebServerConfiguration),
        title = MR.strings.webserver.stable,
        viewState = viewModel.viewState.collectAsState().value,
        onAction = viewModel::onAction,
        testContent = { }
    ) {

        item {
            //switch to enable http server
            SwitchListItem(
                text = MR.strings.enableHTTPApi.stable,
                modifier = Modifier.testTag(TestTag.ServerSwitch),
                isChecked = viewModel.isHttpServerEnabled.collectAsState().value,
                onCheckedChange = viewModel::toggleHttpServerEnabled
            )
        }

        item {
            //visibility of server settings
            AnimatedVisibility(
                enter = expandVertically(),
                exit = shrinkVertically(),
                visible = viewModel.isHttpServerSettingsVisible.collectAsState().value
            ) {

                Column {

                    //port of server
                    TextFieldListItem(
                        label = MR.strings.port.stable,
                        modifier = Modifier.testTag(TestTag.Port),
                        value = viewModel.httpServerPortText.collectAsState().value,
                        onValueChange = viewModel::changeHttpServerPort,
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                    )

                    WebserverSSL(viewModel)

                }

            }
        }
    }

}

/**
 * SSL Settings
 * ON/OFF
 * certificate selection
 */
@Composable
private fun WebserverSSL(viewModel: WebServerConfigurationViewModel) {


    //switch to enabled http ssl
    SwitchListItem(
        text = MR.strings.enableSSL.stable,
        modifier = Modifier.testTag(TestTag.SSLSwitch),
        isChecked = viewModel.isHttpServerSSLEnabled.collectAsState().value,
        onCheckedChange = viewModel::toggleHttpServerSSLEnabled
    )

    //visibility of choose certificate button for ssl
    AnimatedVisibility(
        enter = expandVertically(),
        exit = shrinkVertically(),
        visible = viewModel.isHttpServerSSLCertificateVisible.collectAsState().value
    ) {

        Column {

            ListElement(
                modifier = Modifier
                    .testTag(TestTag.WebServerSSLWiki)
                    .clickable(onClick = viewModel::openWebServerSSLWiki),
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Link,
                        contentDescription = MR.strings.sslWiki.stable
                    )
                },
                text = { Text(MR.strings.sslWiki.stable) },
                secondaryText = { Text(MR.strings.sslWikiInfo.stable) }
            )

            //button to select ssl certificate
            FilledTonalButtonListItem(
                text = MR.strings.chooseCertificate.stable,
                modifier = Modifier.testTag(TestTag.CertificateButton),
                onClick = viewModel::selectSSLCertificate
            )

            AnimatedVisibility(
                enter = expandVertically(),
                exit = shrinkVertically(),
                visible = viewModel.isHttpServerSSLKeyStoreFileTextVisible.collectAsState().value
            ) {
                InformationListElement(
                    text = translate(
                        resource = MR.strings.currentlySelectedCertificate.stable,
                        viewModel.httpServerSSLKeyStoreFileText.collectAsState().value?.name ?: ""
                    )
                )
            }

            //text field to change key store password
            TextFieldListItemVisibility(
                label = MR.strings.keyStorePassword.stable,
                value = viewModel.httpServerSSLKeyStorePassword.collectAsState().value,
                onValueChange = viewModel::changeHttpSSLKeyStorePassword,
                isLastItem = false
            )

            //textField to change key alias
            TextFieldListItemVisibility(
                label = MR.strings.keyStoreKeyAlias.stable,
                value = viewModel.httpServerSSLKeyAlias.collectAsState().value,
                onValueChange = viewModel::changeHttpSSLKeyAlias,
                isLastItem = false
            )

            //textField to change key password
            TextFieldListItemVisibility(
                label = MR.strings.keyStoreKeyPassword.stable,
                value = viewModel.httpServerSSLKeyPassword.collectAsState().value,
                onValueChange = viewModel::changeHttpSSLKeyPassword
            )

        }
    }

}