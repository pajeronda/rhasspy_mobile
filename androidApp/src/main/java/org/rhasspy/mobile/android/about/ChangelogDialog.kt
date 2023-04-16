package org.rhasspy.mobile.android.about

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.rhasspy.mobile.BuildKonfig
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.content.elements.Text
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.data.resource.stable

/**
 * button to open changelog dialog
 */
@Preview(showBackground = true)
@Composable
fun ChangelogDialogButton() {

    var openDialog by rememberSaveable { mutableStateOf(false) }

    OutlinedButton(
        onClick = { openDialog = true },
        modifier = Modifier.testTag(TestTag.DialogChangelogButton)
    ) {
        Text(MR.strings.changelog.stable)
    }

    if (openDialog) {
        ChangelogDialog {
            openDialog = false
        }
    }

}

/**
 * Displays changelog as text in a dialog
 */
@Preview(showBackground = true)
@Composable
private fun ChangelogDialog(onDismissRequest: () -> Unit = {}) {

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = onDismissRequest,
                modifier = Modifier.testTag(TestTag.DialogOk)
            ) {
                Text(MR.strings.close.stable)
            }
        },
        title = {
            Text(MR.strings.changelog.stable)
        },
        text = {
            Column(
                modifier = Modifier
                    .testTag(TestTag.DialogChangelog)
                    .verticalScroll(rememberScrollState()),
            ) {
                BuildKonfig.changelog.split("\\\\")
                    .map { it.replace("\n", "") }
                    .filter { it.isNotEmpty() }
                    .forEach {
                        Text(text = "· $it")
                    }
            }
        }
    )

}