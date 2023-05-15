package org.rhasspy.mobile.android.about

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import kotlinx.collections.immutable.persistentListOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.rhasspy.mobile.android.utils.FlakyTest
import org.rhasspy.mobile.android.utils.onNodeWithTag
import org.rhasspy.mobile.ui.TestTag

/**
 * Test visibility and close button of changelog dialog
 */
class ChangelogDialogTest : FlakyTest() {

    @get: Rule(order = 0)
    val composeTestRule = createComposeRule()

    @Before
    fun setUp() {

        composeTestRule.setContent {
            ChangelogDialogButton(persistentListOf())
        }

    }

    /**
     * User clicks button
     * Dialog with text opens
     * User clicks ok button
     * Dialog closes
     */
    @Test
    fun testDialog() {
        //User clicks button
        composeTestRule.onNodeWithTag(TestTag.DialogChangelogButton).performClick()
        //Dialog with text opens
        composeTestRule.onNodeWithTag(TestTag.DialogChangelog).assertExists()
        //User clicks ok button
        composeTestRule.onNodeWithTag(TestTag.DialogOk).performClick()
        //Dialog closes
        composeTestRule.onNodeWithTag(TestTag.DialogChangelog).assertDoesNotExist()
    }

}