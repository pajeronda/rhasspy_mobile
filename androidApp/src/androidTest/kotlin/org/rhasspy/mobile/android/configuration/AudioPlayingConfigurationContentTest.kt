package org.rhasspy.mobile.android.configuration

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.component.get
import org.rhasspy.mobile.android.utils.FlakyTest
import org.rhasspy.mobile.android.utils.TestContentProvider
import org.rhasspy.mobile.android.utils.onListItemRadioButton
import org.rhasspy.mobile.android.utils.onListItemSwitch
import org.rhasspy.mobile.android.utils.onNodeWithTag
import org.rhasspy.mobile.android.utils.saveBottomAppBar
import org.rhasspy.mobile.data.service.option.AudioOutputOption
import org.rhasspy.mobile.data.service.option.AudioPlayingOption
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.configuration.AudioPlayingConfigurationScreen
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiEvent.Action.Save
import org.rhasspy.mobile.viewmodel.configuration.audioplaying.AudioPlayingConfigurationUiEvent.Change.SelectEditAudioPlayingOption
import org.rhasspy.mobile.viewmodel.configuration.audioplaying.AudioPlayingConfigurationViewModel
import kotlin.test.assertEquals

class AudioPlayingConfigurationContentTest : FlakyTest() {

    @get: Rule(order = 0)
    val composeTestRule = createComposeRule()

    private val viewModel = get<AudioPlayingConfigurationViewModel>()

    @Before
    fun setUp() {

        composeTestRule.setContent {
            TestContentProvider {
                AudioPlayingConfigurationScreen()
            }
        }

    }

    /**
     * option disable is set
     * User clicks option remote http
     * new option is selected
     *
     * Endpoint visible
     * custom endpoint switch visible
     *
     * switch is off
     * endpoint cannot be changed
     *
     * user clicks switch
     * switch is on
     * endpoint can be changed
     *
     * User clicks save
     * option is saved to remote http
     * endpoint is saved
     * use custom endpoint is saved
     */
    @Test
    fun testEndpoint() = runTest {
        viewModel.onEvent(SelectEditAudioPlayingOption(AudioPlayingOption.Disabled))
        viewModel.onEvent(Save)
        composeTestRule.awaitIdle()

        val textInputTest = "endpointTestInput"

        //option disable is set
        composeTestRule.onNodeWithTag(AudioPlayingOption.Disabled).onListItemRadioButton()
            .assertIsSelected()

        //User clicks option remote http
        composeTestRule.onNodeWithTag(AudioPlayingOption.RemoteHTTP).onListItemRadioButton()
            .performClick()
        //new option is selected
        composeTestRule.awaitIdle()
        assertEquals(
            AudioPlayingOption.RemoteHTTP,
            viewModel.viewState.value.editData.audioPlayingOption
        )
        composeTestRule.onNodeWithTag(AudioPlayingOption.RemoteHTTP).onListItemRadioButton()
            .assertIsSelected()

        //Endpoint visible
        composeTestRule.onNodeWithTag(TestTag.Endpoint).assertExists()
        //custom endpoint switch visible
        composeTestRule.onNodeWithTag(TestTag.CustomEndpointSwitch).assertExists()

        //switch is off
        composeTestRule.onNodeWithTag(TestTag.CustomEndpointSwitch).performScrollTo()
            .onListItemSwitch().assertIsOff()
        //endpoint cannot be changed
        composeTestRule.onNodeWithTag(TestTag.Endpoint).assertIsNotEnabled()

        //user clicks switch
        composeTestRule.onNodeWithTag(TestTag.CustomEndpointSwitch).performClick()
        //switch is on
        composeTestRule.onNodeWithTag(TestTag.CustomEndpointSwitch).onListItemSwitch().assertIsOn()
        //endpoint can be changed
        composeTestRule.onNodeWithTag(TestTag.Endpoint).assertIsEnabled()
        composeTestRule.onNodeWithTag(TestTag.Endpoint).performScrollTo().performClick()
        composeTestRule.awaitIdle()
        composeTestRule.onNodeWithTag(TestTag.Endpoint).performTextClearance()
        composeTestRule.onNodeWithTag(TestTag.Endpoint).performTextInput(textInputTest)
        composeTestRule.awaitIdle()
        assertEquals(textInputTest, viewModel.viewState.value.editData.audioPlayingHttpEndpoint)

        //User clicks save
        composeTestRule.saveBottomAppBar()
        AudioPlayingConfigurationViewModel(get()).viewState.value.editData.also {
            //option is saved to remote http
            assertEquals(AudioPlayingOption.RemoteHTTP, it.audioPlayingOption)
            //endpoint is saved
            assertEquals(textInputTest, it.audioPlayingHttpEndpoint)
            //use custom endpoint is saved
            assertEquals(true, it.isUseCustomAudioPlayingHttpEndpoint)
        }
    }

    /**
     * output is set to sound
     *
     * option disable is set
     * output options not visible
     * User clicks option local
     * new option is selected
     *
     * output options visible
     * option sound is set
     *
     * user clicks option notification
     * option notification is selected
     *
     * User clicks save
     * option is saved to local
     * option notification is saved
     */
    @Test
    fun testLocalOutput() = runTest {
        viewModel.onEvent(SelectEditAudioPlayingOption(AudioPlayingOption.Disabled))
        viewModel.onEvent(Save)
        composeTestRule.awaitIdle()

        //output is set to sound
        assertEquals(AudioOutputOption.Sound, viewModel.viewState.value.editData.audioOutputOption)

        //option disable is set
        composeTestRule.onNodeWithTag(AudioPlayingOption.Disabled, true).performScrollTo()
            .onListItemRadioButton().assertIsSelected()
        //output options not visible
        composeTestRule.onNodeWithTag(TestTag.AudioOutputOptions).assertDoesNotExist()

        //User clicks option local
        composeTestRule.onNodeWithTag(AudioPlayingOption.Local).performClick()
        //new option is selected
        composeTestRule.awaitIdle()
        assertEquals(
            AudioPlayingOption.Local,
            viewModel.viewState.value.editData.audioPlayingOption
        )

        //output options visible
        composeTestRule.onNodeWithTag(TestTag.AudioOutputOptions).assertIsDisplayed()
        //option sound is set
        composeTestRule.onNodeWithTag(AudioOutputOption.Sound, true).onListItemRadioButton()
            .assertIsSelected()

        //user clicks option notification
        composeTestRule.onNodeWithTag(AudioOutputOption.Notification).performClick()
        //option notification is selected
        composeTestRule.onNodeWithTag(AudioOutputOption.Notification, true).onListItemRadioButton()
            .assertIsSelected()

        //User clicks save
        composeTestRule.saveBottomAppBar()
        AudioPlayingConfigurationViewModel(get()).viewState.value.editData.also {
            //option is saved to local
            assertEquals(AudioPlayingOption.Local, it.audioPlayingOption)
            //option notification is saved
            assertEquals(AudioOutputOption.Notification, it.audioOutputOption)
        }
    }
}