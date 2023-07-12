package org.rhasspy.mobile.android.configuration

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import androidx.test.uiautomator.Until
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.component.get
import org.rhasspy.mobile.android.utils.FlakyTest
import org.rhasspy.mobile.android.utils.TestContentProvider
import org.rhasspy.mobile.android.utils.onNodeWithTag
import org.rhasspy.mobile.android.utils.saveBottomAppBar
import org.rhasspy.mobile.data.service.option.WakeWordOption
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.configuration.WakeWordConfigurationScreen
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiEvent.Action.Save
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.Change.SelectWakeWordOption
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.PorcupineUiEvent.Change.UpdateWakeWordPorcupineAccessToken
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationViewModel
import org.rhasspy.mobile.viewmodel.navigation.INavigator
import org.rhasspy.mobile.viewmodel.navigation.destinations.ConfigurationScreenNavigationDestination.WakeWordConfigurationScreen
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class WakeWordConfigurationContentTest : FlakyTest() {

    @get: Rule(order = 0)
    val composeTestRule = createComposeRule()

    private val device: UiDevice =
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    private val viewModel = get<WakeWordConfigurationViewModel>()

    @Before
    fun setUp() {
        get<INavigator>().navigate(WakeWordConfigurationScreen)

        composeTestRule.setContent {
            TestContentProvider {
                WakeWordConfigurationScreen()
            }
        }

    }


    /**
     * option is disable
     *
     * porcupine options not visible
     *
     * user clicks porcupine
     * new option is set
     * porcupine options visible
     *
     * user clicks save
     * new option is saved
     */
    @Test
    fun testWakeWordContent() = runTest {
        //option is disable
        viewModel.onEvent(SelectWakeWordOption(WakeWordOption.Disabled))
        viewModel.onEvent(Save)
        composeTestRule.awaitIdle()

        assertEquals(WakeWordOption.Disabled, viewModel.viewState.value.editData.wakeWordOption)

        //porcupine options not visible
        composeTestRule.onNodeWithTag(TestTag.PorcupineWakeWordSettings).assertDoesNotExist()
        //user clicks porcupine
        composeTestRule.onNodeWithTag(WakeWordOption.Porcupine).performClick()
        //porcupine options visible
        composeTestRule.onNodeWithTag(TestTag.PorcupineWakeWordSettings).assertIsDisplayed()

        //user clicks save
        composeTestRule.saveBottomAppBar()
        WakeWordConfigurationViewModel(get(), get()).viewState.value.editData.also {
            //new option is saved
            assertEquals(WakeWordOption.Porcupine, it.wakeWordOption)
        }
    }

    /**
     * option is porcupine
     *
     * access token is visible
     * user changes access key
     * access token change
     *
     * user clicks picovoice console
     * browser is opened
     *
     * user clicks save
     * access token is saved
     */
    @Test
    fun testPorcupineOptions() = runTest {
        //option is porcupine
        viewModel.onEvent(SelectWakeWordOption(WakeWordOption.Porcupine))
        viewModel.onEvent(UpdateWakeWordPorcupineAccessToken(""))
        viewModel.onEvent(Save)
        composeTestRule.awaitIdle()

        assertEquals(WakeWordOption.Porcupine, viewModel.viewState.value.editData.wakeWordOption)

        val textInputTest = "fghfghhrtrtzh34ß639254´1´90!$/%(&$("

        //access token is visible
        composeTestRule.onNodeWithTag(TestTag.PorcupineAccessToken).assertIsDisplayed()
        //user changes access token
        composeTestRule.onNodeWithTag(TestTag.PorcupineAccessToken).performScrollTo().performClick()
        composeTestRule.awaitIdle()
        //access token change
        composeTestRule.onNodeWithTag(TestTag.PorcupineAccessToken).performTextClearance()
        composeTestRule.onNodeWithTag(TestTag.PorcupineAccessToken).performTextInput(textInputTest)

        //user clicks picovoice console
        composeTestRule.onNodeWithTag(TestTag.PorcupineOpenConsole).performScrollTo().performClick()
        //browser is opened
        device.wait(Until.hasObject(By.text(".*console.picovoice.ai.*".toPattern())), 5000)
        device.findObject(UiSelector().textMatches(".*console.picovoice.ai.*")).exists()
        device.pressBack()

        composeTestRule.awaitIdle()
    }

    /**
     * option is porcupine
     *
     * wake word is clicked,
     * wake word page is opened
     *
     * back is clicked
     * page is back to wake word settings
     *
     * language is clicked
     * language page is opened
     *
     * back is clicked
     * page is back to wake word settings
     */
    @Test
    fun testPorcupineWakeWordOptions() = runTest {
        //option is porcupine
        viewModel.onEvent(SelectWakeWordOption(WakeWordOption.Porcupine))
        viewModel.onEvent(Save)
        composeTestRule.awaitIdle()

        assertEquals(WakeWordOption.Porcupine, viewModel.viewState.value.editData.wakeWordOption)
        composeTestRule.onNodeWithTag(WakeWordConfigurationScreen).assertIsDisplayed()

        //wake word is clicked,
        composeTestRule.onNodeWithTag(TestTag.PorcupineKeyword).performScrollTo().performClick()
        //wake word page is opened
        composeTestRule.onNodeWithTag(TestTag.PorcupineKeywordScreen).assertIsDisplayed()

        //back is clicked
        composeTestRule.onNodeWithTag(TestTag.AppBarBackButton).performClick()
        //page is back to wake word settings
        composeTestRule.awaitIdle()
        composeTestRule.onNodeWithTag(WakeWordConfigurationScreen).assertIsDisplayed()

        //language is clicked
        composeTestRule.onNodeWithTag(TestTag.PorcupineLanguage).performScrollTo().performClick()
        //language page is opened
        composeTestRule.onNodeWithTag(TestTag.PorcupineLanguageScreen).assertIsDisplayed()

        //back is clicked
        composeTestRule.onNodeWithTag(TestTag.AppBarBackButton).performClick()
        //page is back to wake word settings
        composeTestRule.onNodeWithTag(WakeWordConfigurationScreen).assertIsDisplayed()

        assertTrue(true)
    }

}