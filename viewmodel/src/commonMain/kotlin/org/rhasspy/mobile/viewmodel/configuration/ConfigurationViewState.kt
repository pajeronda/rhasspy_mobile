package org.rhasspy.mobile.viewmodel.configuration

import androidx.compose.runtime.Stable
import org.rhasspy.mobile.viewmodel.screens.configuration.ServiceViewState

@Stable
data class ConfigurationViewState(
    val serviceViewState: ServiceViewState,
    val hasUnsavedChanges: Boolean = false,
    val isOpenServiceStateDialogEnabled: Boolean = false,
    val dialogState: DialogState? = null
) {

    sealed interface DialogState {

        data object UnsavedChangesDialogState : DialogState
        data class ServiceStateDialogState(val dialogText: Any) : DialogState

    }

}