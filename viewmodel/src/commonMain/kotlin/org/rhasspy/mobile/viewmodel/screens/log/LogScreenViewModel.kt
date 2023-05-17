package org.rhasspy.mobile.viewmodel.screens.log

import androidx.compose.runtime.Stable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.logic.logger.FileLogger
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.viewmodel.KViewModel
import org.rhasspy.mobile.viewmodel.screens.log.LogScreenUiEvent.*
import org.rhasspy.mobile.viewmodel.screens.log.LogScreenUiEvent.Action.SaveLogFile
import org.rhasspy.mobile.viewmodel.screens.log.LogScreenUiEvent.Action.ShareLogFile
import org.rhasspy.mobile.viewmodel.screens.log.LogScreenUiEvent.Change.ToggleListAutoScroll
import org.rhasspy.mobile.viewmodel.screens.log.LogScreenUiEvent.Consumed.ShowSnackBar

@Stable
class LogScreenViewModel(
    viewStateCreator: LogScreenViewStateCreator
) : KViewModel() {

    private val _viewState: MutableStateFlow<LogScreenViewState> = viewStateCreator()
    val viewState = _viewState.readOnly

    fun onEvent(event: LogScreenUiEvent) {
        when (event) {
            is Change -> onChange(event)
            is Action -> onAction(event)
            is Consumed -> onConsumed(event)
        }
    }

    private fun onChange(change: Change) {
        when (change) {
            ToggleListAutoScroll -> AppSetting.isLogAutoscroll.value = !AppSetting.isLogAutoscroll.value
        }
    }

    private fun onAction(action: Action) {
        when (action) {
            SaveLogFile -> {
                viewModelScope.launch(Dispatchers.IO) {
                    if (!FileLogger.saveLogFile()) {
                        _viewState.update {
                            it.copy(snackBarText = MR.strings.saveLogFileFailed.stable)
                        }
                    }
                }
            }

            ShareLogFile -> {
                if (!FileLogger.shareLogFile()) {
                    _viewState.update {
                        it.copy(snackBarText = MR.strings.shareLogFileFailed.stable)
                    }
                }
            }
        }
    }

    private fun onConsumed(consumed: Consumed) {
        _viewState.update {
            when (consumed) {
                ShowSnackBar -> it.copy(snackBarText = null)
            }
        }
    }

}