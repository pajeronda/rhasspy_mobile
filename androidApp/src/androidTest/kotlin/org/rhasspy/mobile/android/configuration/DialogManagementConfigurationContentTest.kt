package org.rhasspy.mobile.android.configuration

import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.component.get
import org.rhasspy.mobile.android.utils.*
import org.rhasspy.mobile.data.service.option.DialogManagementOption
import org.rhasspy.mobile.ui.configuration.DialogManagementConfigurationScreen
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiEvent.Action.Save
import org.rhasspy.mobile.viewmodel.configuration.dialogmanagement.DialogManagementConfigurationUiEvent.Change.SelectDialogManagementOption
import org.rhasspy.mobile.viewmodel.configuration.dialogmanagement.DialogManagementConfigurationViewModel
import kotlin.test.assertEquals

class DialogManagementConfigurationContentTest : FlakyTest() {

    @get: Rule(order = 0)
    val composeTestRule = createComposeRule()

    private val viewModel = get<DialogManagementConfigurationViewModel>()

    @Before
    fun setUp() {

        composeTestRule.setContent {
            TestContentProvider {
                DialogManagementConfigurationScreen()
            }
        }

    }

    /**
     * option disable is set
     * User clicks option local
     * new option is selected
     *
     * User clicks save
     * option is saved to local
     */
    @Test
    fun testEndpoint() = runTest {
        viewModel.onEvent(SelectDialogManagementOption(DialogManagementOption.Disabled))
        viewModel.onEvent(Save)
        composeTestRule.awaitIdle()

        //option disable is set
        composeTestRule.onNodeWithTag(DialogManagementOption.Disabled, true).onListItemRadioButton().assertIsSelected()
        //User clicks option local
        composeTestRule.onNodeWithTag(DialogManagementOption.Local).performClick()
        composeTestRule.awaitIdle()
        //new option is selected
        composeTestRule.onNodeWithTag(DialogManagementOption.Local, true).onListItemRadioButton().assertIsSelected()

        //User clicks save
        composeTestRule.saveBottomAppBar()
        DialogManagementConfigurationViewModel(get()).viewState.value.editData.also {
            //option is saved to local
            assertEquals(DialogManagementOption.Local, it.dialogManagementOption)
        }
    }

}