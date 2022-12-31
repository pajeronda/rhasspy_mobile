package org.rhasspy.mobile.android.configuration.content.porcupine

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onChildAt
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.rhasspy.mobile.android.main.LocalNavController
import org.rhasspy.mobile.android.onNodeWithTag
import org.rhasspy.mobile.data.PorcupineLanguageOptions
import org.rhasspy.mobile.viewmodel.configuration.WakeWordConfigurationViewModel
import kotlin.test.assertEquals

class PorcupineLanguageScreenTest {

    @get: Rule
    val composeTestRule = createComposeRule()

    private val viewModel = WakeWordConfigurationViewModel()

    @Before
    fun setUp() {

        composeTestRule.setContent {
            val navController = rememberNavController()

            CompositionLocalProvider(
                LocalNavController provides navController
            ) {
                PorcupineLanguageScreen(viewModel)
            }
        }

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
    fun testContent() = runBlocking {
        //English is saved
        viewModel.selectWakeWordPorcupineLanguage(PorcupineLanguageOptions.EN)
        viewModel.save()
        assertEquals(PorcupineLanguageOptions.EN, viewModel.wakeWordPorcupineLanguage.value)

        //english is selected
        composeTestRule.onNodeWithTag(PorcupineLanguageOptions.EN).onChildAt(0).assertIsSelected()

        //user clicks german
        composeTestRule.onNodeWithTag(PorcupineLanguageOptions.DE).performClick()
        //german is selected
        composeTestRule.onNodeWithTag(PorcupineLanguageOptions.DE).onChildAt(0).assertIsSelected()

        //save is invoked
        viewModel.save()
        val newViewModel = WakeWordConfigurationViewModel()
        //german is saved
        assertEquals(PorcupineLanguageOptions.DE, newViewModel.wakeWordPorcupineLanguage.value)
    }
}