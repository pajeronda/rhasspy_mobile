package org.rhasspy.mobile.android.configuration.porcupine

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.performClick
import com.adevinta.android.barista.rule.flaky.AllowFlaky
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.koin.core.component.get
import org.rhasspy.mobile.android.utils.FlakyTest
import org.rhasspy.mobile.android.utils.onListItemRadioButton
import org.rhasspy.mobile.android.utils.onNodeWithTag
import org.rhasspy.mobile.data.service.option.PorcupineLanguageOption
import org.rhasspy.mobile.ui.configuration.domains.wake.porcupine.PorcupineLanguageScreen
import org.rhasspy.mobile.viewmodel.configuration.connections.IConfigurationUiEvent.Action.Save
import org.rhasspy.mobile.viewmodel.configuration.wake.WakeDomainConfigurationUiEvent.PorcupineUiEvent.Change.SelectWakeDomainPorcupineLanguage
import org.rhasspy.mobile.viewmodel.configuration.wake.WakeDomainConfigurationViewModel
import kotlin.test.assertEquals

class PorcupineLanguageScreenTest : FlakyTest() {

    private val viewModel = get<WakeDomainConfigurationViewModel>()

    @Composable
    override fun ComposableContent() {
        PorcupineLanguageScreen(viewModel)
    }

    /**
     * English is saved
     *
     * english is selected
     *
     * user clicks german
     * german is selected
     *
     * save is invoked
     * german is saved
     */
    @Test
    @AllowFlaky
    fun testContent() = runTest {
        setupContent()

        //English is saved
        viewModel.onEvent(SelectWakeDomainPorcupineLanguage(PorcupineLanguageOption.EN))
        viewModel.onEvent(Save)
        composeTestRule.awaitIdle()
        val editData = viewModel.viewState.value.editData.wakeWordPorcupineConfigurationData

        composeTestRule.awaitIdle()
        assertEquals(PorcupineLanguageOption.EN, editData.porcupineLanguage)

        //english is selected
        composeTestRule.awaitIdle()
        composeTestRule.onNodeWithTag(PorcupineLanguageOption.EN).onListItemRadioButton()
            .assertIsSelected()

        //user clicks german
        composeTestRule.onNodeWithTag(PorcupineLanguageOption.DE).performClick()
        //german is selected
        composeTestRule.awaitIdle()
        composeTestRule.onNodeWithTag(PorcupineLanguageOption.DE).onListItemRadioButton().assertIsSelected()
    }
}