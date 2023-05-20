package org.rhasspy.mobile.android.configuration.content

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.adevinta.android.barista.rule.flaky.AllowFlaky
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.component.get
import org.rhasspy.mobile.android.utils.*
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiEvent.Action.Save
import org.rhasspy.mobile.viewmodel.configuration.webserver.WebServerConfigurationUiEvent.Change.SetHttpServerEnabled
import org.rhasspy.mobile.viewmodel.configuration.webserver.WebServerConfigurationUiEvent.Change.SetHttpServerSSLEnabled
import org.rhasspy.mobile.viewmodel.configuration.webserver.WebServerConfigurationViewModel
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class WebServerServiceConfigurationContentTest : FlakyTest() {

    @get: Rule(order = 0)
    val composeTestRule = createComposeRule()

    private val viewModel = get<WebServerConfigurationViewModel>()

    @Before
    fun setUp() {

        composeTestRule.setContent {
            TestContentProvider {
                WebServerConfigurationContent()
            }
        }

    }

    /**
     * http api is disabled
     * switch is off
     * settings not visible
     *
     * user clicks switch
     * http api is enabled
     * switch is pn
     * settings visible
     *
     * port is changed
     *
     * enable ssl is off
     * enable ssl switch is off
     * certificate button not visible
     *
     * user clicks enable ssl
     * ssl is on
     * enable ssl switch is on
     * certificate button visible
     *
     * user click save
     * enable http api is saved
     * port is saved
     * enable ssl is saved
     */
    @Test
    @AllowFlaky(attempts = 5)
    fun testHttpContent() = runTest {
        viewModel.onEvent(SetHttpServerEnabled(false))
        viewModel.onEvent(SetHttpServerSSLEnabled(false))
        viewModel.onAction(Save)
        composeTestRule.awaitSaved(viewModel)
        composeTestRule.awaitIdle()
        val viewState = viewModel.viewState.value.editViewState

        val textInputTest = "6541"

        //http api is disabled
        assertFalse { viewState.value.isHttpServerEnabled }
        //switch is off
        composeTestRule.onNodeWithTag(TestTag.ServerSwitch).onListItemSwitch().assertIsOff()
        //settings not visible
        composeTestRule.onNodeWithTag(TestTag.Port).assertDoesNotExist()
        composeTestRule.onNodeWithTag(TestTag.SSLSwitch).assertDoesNotExist()
        composeTestRule.onNodeWithTag(TestTag.CertificateButton).assertDoesNotExist()

        //user clicks switch
        composeTestRule.onNodeWithTag(TestTag.ServerSwitch).performClick()
        //http api is enabled
        assertTrue { viewState.value.isHttpServerEnabled }
        //switch is on
        composeTestRule.onNodeWithTag(TestTag.ServerSwitch).onListItemSwitch().assertIsOn()
        //settings visible
        composeTestRule.onNodeWithTag(TestTag.Port).assertExists()
        composeTestRule.onNodeWithTag(TestTag.SSLSwitch).assertExists()

        //port is changed
        composeTestRule.onNodeWithTag(TestTag.Port).performScrollTo().performClick()
        composeTestRule.onNodeWithTag(TestTag.Port).performTextReplacement(textInputTest)

        //enable ssl is off
        assertFalse { viewState.value.isHttpServerSSLEnabled }
        //enable ssl switch is off
        composeTestRule.onNodeWithTag(TestTag.SSLSwitch).onListItemSwitch().assertIsOff()
        //certificate button not visible
        composeTestRule.onNodeWithTag(TestTag.CertificateButton).assertDoesNotExist()

        //user clicks enable ssl
        composeTestRule.onNodeWithTag(TestTag.SSLSwitch).performScrollTo().performClick()
        //ssl is on
        assertTrue { viewState.value.isHttpServerSSLEnabled }
        //enable ssl switch is on
        composeTestRule.onNodeWithTag(TestTag.SSLSwitch).onListItemSwitch().assertIsOn()
        //certificate button visible
        composeTestRule.onNodeWithTag(TestTag.CertificateButton).assertExists()

        //user click save
        composeTestRule.saveBottomAppBar(viewModel)
        WebServerConfigurationViewModel(get()).viewState.value.editViewState.value.also {
            //enable http api is saved
            assertEquals(true, it.isHttpServerEnabled)
            //port is saved
            assertEquals(textInputTest, it.httpServerPortText)
            //enable ssl is saved
            assertEquals(true, it.isHttpServerSSLEnabled)
        }
    }
}