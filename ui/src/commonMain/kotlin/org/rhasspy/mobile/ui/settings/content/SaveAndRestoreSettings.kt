package org.rhasspy.mobile.ui.settings.content

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.LocalSnackBarHostState
import org.rhasspy.mobile.ui.LocalViewModelFactory
import org.rhasspy.mobile.ui.Screen
import org.rhasspy.mobile.ui.content.elements.Dialog
import org.rhasspy.mobile.ui.content.elements.Icon
import org.rhasspy.mobile.ui.content.elements.Text
import org.rhasspy.mobile.ui.content.elements.translate
import org.rhasspy.mobile.ui.content.list.ListElement
import org.rhasspy.mobile.ui.settings.SettingsScreenItemContent
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.viewmodel.navigation.destinations.SettingsScreenDestination.SaveAndRestoreSettings
import org.rhasspy.mobile.viewmodel.settings.saveandrestore.SaveAndRestoreSettingsUiEvent
import org.rhasspy.mobile.viewmodel.settings.saveandrestore.SaveAndRestoreSettingsUiEvent.Action.*
import org.rhasspy.mobile.viewmodel.settings.saveandrestore.SaveAndRestoreSettingsUiEvent.Consumed.ShowSnackBar
import org.rhasspy.mobile.viewmodel.settings.saveandrestore.SaveAndRestoreSettingsViewModel

/**
 * to save and restore settings
 */

@Composable
fun SaveAndRestoreSettingsContent() {
    val viewModel: SaveAndRestoreSettingsViewModel = LocalViewModelFactory.current.getViewModel()

    Screen(viewModel) {
        val viewState by viewModel.viewState.collectAsState()

        val snackBarHostState = LocalSnackBarHostState.current
        val snackBarText = viewState.snackBarText?.let { translate(it) }

        LaunchedEffect(snackBarText) {
            snackBarText?.also {
                snackBarHostState.showSnackbar(message = it)
                viewModel.onEvent(ShowSnackBar)
            }
        }

        SettingsScreenItemContent(
            modifier = Modifier.testTag(SaveAndRestoreSettings),
            title = MR.strings.saveAndRestoreSettings.stable,
            onBackClick = { viewModel.onEvent(BackClick) }
        ) {

            //Save Settings
            SaveSettings(
                isSaveSettingsToFileDialogVisible = viewState.isSaveSettingsToFileDialogVisible,
                onEvent = viewModel::onEvent
            )

            //Restore Settings
            RestoreSettings(
                isRestoreSettingsFromFileDialogVisible = viewState.isRestoreSettingsFromFileDialogVisible,
                onEvent = viewModel::onEvent
            )

            //Share Settings
            ShareSettings(viewModel::onEvent)
        }
    }

}

/**
 * Save Settings
 * Shows warning Dialog that the file contains sensitive information
 */
@Composable
private fun SaveSettings(
    isSaveSettingsToFileDialogVisible: Boolean,
    onEvent: (SaveAndRestoreSettingsUiEvent) -> Unit
) {

    //save settings
    ListElement(
        modifier = Modifier.clickable { onEvent(ExportSettingsFile) },
        icon = {
            Icon(
                imageVector = Icons.Filled.Save,
                contentDescription = MR.strings.save.stable
            )
        },
        text = {
            Text(MR.strings.save.stable)
        },
        secondaryText = {
            Text(MR.strings.saveSettingsText.stable)
        }
    )

    //save settings dialog
    if (isSaveSettingsToFileDialogVisible) {
        SaveSettingsDialog(
            onConfirm = { onEvent(ExportSettingsFileConfirmation) },
            onDismiss = { onEvent(ExportSettingsFileDismiss) }
        )
    }
}

/**
 * Restore settings
 * shows dialog that current settings will be overwritten
 */
@Composable
private fun RestoreSettings(
    isRestoreSettingsFromFileDialogVisible: Boolean,
    onEvent: (SaveAndRestoreSettingsUiEvent) -> Unit
) {

    //restore settings
    ListElement(
        modifier = Modifier.clickable { onEvent(RestoreSettingsFromFile) },
        icon = {
            Icon(
                imageVector = Icons.Filled.Restore,
                contentDescription = MR.strings.restore.stable
            )
        },
        text = {
            Text(MR.strings.restore.stable)
        },
        secondaryText = {
            Text(MR.strings.restoreSettingsText.stable)
        }
    )

    //restore settings dialog
    if (isRestoreSettingsFromFileDialogVisible) {

        RestoreSettingsDialog(
            onConfirm = { onEvent(RestoreSettingsFromFileConfirmation) },
            onDismiss = { onEvent(RestoreSettingsFromFileDismiss) }
        )

    }
}

@Composable
private fun ShareSettings(onEvent: (SaveAndRestoreSettingsUiEvent) -> Unit) {

    //restore settings
    ListElement(
        modifier = Modifier.clickable(onClick = { onEvent(ShareSettingsFile) }),
        icon = {
            Icon(
                imageVector = Icons.Filled.Share,
                contentDescription = MR.strings.share.stable
            )
        },
        text = {
            Text(MR.strings.share.stable)
        },
        secondaryText = {
            Text(MR.strings.shareSettingsText.stable)
        }
    )

}

/**
 * dialog to save settings
 */
@Composable
private fun SaveSettingsDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {

    Dialog(
        onDismissRequest = onDismiss,
        headline = {
            Text(MR.strings.saveSettings.stable)
        },
        supportingText = {
            Text(
                resource = MR.strings.saveSettingsWarningText.stable,
                textAlign = TextAlign.Center
            )
        },
        icon = {
            Icon(
                imageVector = Icons.Filled.Warning,
                contentDescription = MR.strings.warning.stable
            )
        },
        confirmButton = {
            Button(onConfirm) {
                Text(MR.strings.ok.stable)
            }
        },
        dismissButton = {
            OutlinedButton(onDismiss) {
                Text(MR.strings.cancel.stable)
            }
        }
    )

}

/**
 * dialog to restore settings
 */
@Composable
private fun RestoreSettingsDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {

    Dialog(
        onDismissRequest = onDismiss,
        headline = {
            Text(MR.strings.restoreSettings.stable)
        },
        supportingText = {
            Text(
                resource = MR.strings.restoreSettingsWarningText.stable,
                textAlign = TextAlign.Center
            )
        },
        icon = {
            Icon(
                imageVector = Icons.Filled.Warning,
                contentDescription = MR.strings.warning.stable
            )
        },
        confirmButton = {
            Button(onConfirm) {
                Text(MR.strings.ok.stable)
            }
        },
        dismissButton = {
            OutlinedButton(onDismiss) {
                Text(MR.strings.cancel.stable)
            }
        }
    )

}