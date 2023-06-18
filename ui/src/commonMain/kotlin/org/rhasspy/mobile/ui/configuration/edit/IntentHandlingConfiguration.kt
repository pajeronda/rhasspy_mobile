package org.rhasspy.mobile.ui.configuration.edit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.service.option.HomeAssistantIntentHandlingOption
import org.rhasspy.mobile.data.service.option.HomeAssistantIntentHandlingOption.Event
import org.rhasspy.mobile.data.service.option.HomeAssistantIntentHandlingOption.Intent
import org.rhasspy.mobile.data.service.option.IntentHandlingOption
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.LocalViewModelFactory
import org.rhasspy.mobile.ui.Screen
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.configuration.ConfigurationScreenConfig
import org.rhasspy.mobile.ui.configuration.ConfigurationScreenItemContent
import org.rhasspy.mobile.ui.content.elements.RadioButtonsEnumSelection
import org.rhasspy.mobile.ui.content.list.FilledTonalButtonListItem
import org.rhasspy.mobile.ui.content.list.RadioButtonListItem
import org.rhasspy.mobile.ui.content.list.TextFieldListItem
import org.rhasspy.mobile.ui.content.list.TextFieldListItemVisibility
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.ui.theme.ContentPaddingLevel1
import org.rhasspy.mobile.viewmodel.configuration.edit.intenthandling.IntentHandlingConfigurationUiEvent
import org.rhasspy.mobile.viewmodel.configuration.edit.intenthandling.IntentHandlingConfigurationUiEvent.Action.RunIntentHandlingTest
import org.rhasspy.mobile.viewmodel.configuration.edit.intenthandling.IntentHandlingConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.edit.intenthandling.IntentHandlingConfigurationEditViewModel
import org.rhasspy.mobile.viewmodel.configuration.edit.intenthandling.IntentHandlingConfigurationViewState
import org.rhasspy.mobile.viewmodel.navigation.destinations.ConfigurationScreenNavigationDestination.IntentHandlingConfigurationScreen

/**
 * content for intent handling configuration
 * drop down to select option
 * http configuration
 * home assistant configuration
 */
@Composable
fun IntentHandlingConfigurationContent() {
    val viewModel: IntentHandlingConfigurationEditViewModel = LocalViewModelFactory.current.getViewModel()

    Screen(kViewModel = viewModel) {
        val viewState by viewModel.viewState.collectAsState()
        val screen by viewModel.screen.collectAsState()
        val contentViewState by viewState.editViewState.collectAsState()

        ConfigurationScreenItemContent(
            modifier = Modifier.testTag(IntentHandlingConfigurationScreen),
            screenType = screen.destinationType,
            config = ConfigurationScreenConfig(MR.strings.intentHandling.stable),
            viewState = viewState,
            onAction = viewModel::onAction,
            testContent = {
                TestContent(
                    testIntentNameText = contentViewState.testIntentHandlingName,
                    testIntentText = contentViewState.testIntentHandlingText,
                    onEvent = viewModel::onEvent
                )
            }
        ) {

            item {
                IntentHandlingOptionContent(
                    viewState = contentViewState,
                    onEvent = viewModel::onEvent
                )
            }
        }
    }

}

@Composable
private fun IntentHandlingOptionContent(
    viewState: IntentHandlingConfigurationViewState,
    onEvent: (IntentHandlingConfigurationUiEvent) -> Unit
) {

    RadioButtonsEnumSelection(
        modifier = Modifier.testTag(TestTag.IntentHandlingOptions),
        selected = viewState.intentHandlingOption,
        onSelect = { onEvent(SelectIntentHandlingOption(it)) },
        values = viewState.intentHandlingOptionList
    ) { option ->

        when (option) {
            IntentHandlingOption.HomeAssistant -> HomeAssistantOption(
                intentHandlingHassEndpoint = viewState.intentHandlingHassEndpoint,
                intentHandlingHassAccessToken = viewState.intentHandlingHassAccessToken,
                intentHandlingHassOption = viewState.intentHandlingHassOption,
                onEvent = onEvent
            )

            IntentHandlingOption.RemoteHTTP -> RemoteHTTPOption(
                intentHandlingHttpEndpoint = viewState.intentHandlingHttpEndpoint,
                onEvent = onEvent
            )

            else -> {}
        }

    }

}

/**
 * http configuration for intent handling
 * field to set endpoint
 */
@Composable
private fun RemoteHTTPOption(
    intentHandlingHttpEndpoint: String,
    onEvent: (IntentHandlingConfigurationUiEvent) -> Unit
) {

    Column(modifier = Modifier.padding(ContentPaddingLevel1)) {

        //endpoint input field
        TextFieldListItem(
            modifier = Modifier.testTag(TestTag.Endpoint),
            value = intentHandlingHttpEndpoint,
            onValueChange = { onEvent(ChangeIntentHandlingHttpEndpoint(it)) },
            label = MR.strings.remoteURL.stable
        )

    }

}

/**
 * configuration of home assistant intent handling
 * url
 * access token
 * hass event or intent
 */
@Composable
private fun HomeAssistantOption(
    intentHandlingHassEndpoint: String,
    intentHandlingHassAccessToken: String,
    intentHandlingHassOption: HomeAssistantIntentHandlingOption,
    onEvent: (IntentHandlingConfigurationUiEvent) -> Unit
) {

    Column(modifier = Modifier.padding(ContentPaddingLevel1)) {

        //endpoint url
        TextFieldListItem(
            modifier = Modifier.testTag(TestTag.Endpoint),
            value = intentHandlingHassEndpoint,
            onValueChange = { onEvent(ChangeIntentHandlingHassEndpoint(it)) },
            label = MR.strings.hassURL.stable,
            isLastItem = false
        )

        //hass access token
        TextFieldListItemVisibility(
            modifier = Modifier.testTag(TestTag.AccessToken),
            value = intentHandlingHassAccessToken,
            onValueChange = { onEvent(ChangeIntentHandlingHassAccessToken(it)) },
            label = MR.strings.accessToken.stable
        )

        //select hass event or hass intent
        RadioButtonListItem(
            modifier = Modifier.testTag(TestTag.SendEvents),
            text = MR.strings.homeAssistantEvents.stable,
            isChecked = intentHandlingHassOption == Event,
            onClick = { onEvent(SelectIntentHandlingHassOption(Event)) }
        )

        RadioButtonListItem(
            modifier = Modifier.testTag(TestTag.SendIntents),
            text = MR.strings.homeAssistantIntents.stable,
            isChecked = intentHandlingHassOption == Intent,
            onClick = { onEvent(SelectIntentHandlingHassOption(Intent)) }
        )

    }

}

/**
 * show test inputs
 */
@Composable
private fun TestContent(
    testIntentNameText: String,
    testIntentText: String,
    onEvent: (IntentHandlingConfigurationUiEvent) -> Unit
) {

    Column {

        TextFieldListItem(
            modifier = Modifier.testTag(TestTag.IntentNameText),
            value = testIntentNameText,
            onValueChange = { onEvent(UpdateTestIntentHandlingName(it)) },
            label = MR.strings.intentName.stable
        )

        TextFieldListItem(
            modifier = Modifier.testTag(TestTag.IntentText),
            value = testIntentText,
            onValueChange = { onEvent(UpdateTestIntentHandlingText(it)) },
            label = MR.strings.intentText.stable
        )

        FilledTonalButtonListItem(
            text = MR.strings.executeHandleIntent.stable,
            onClick = { onEvent(RunIntentHandlingTest) },
        )

    }

}