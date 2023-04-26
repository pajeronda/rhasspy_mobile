package org.rhasspy.mobile.viewmodel.screens.about

import androidx.compose.runtime.Stable
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.koin.core.component.KoinComponent
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.data.link.LinkType
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.viewmodel.screens.about.AboutScreenUiEvent.Action
import org.rhasspy.mobile.viewmodel.screens.about.AboutScreenUiEvent.Action.OpenSourceCode
import org.rhasspy.mobile.viewmodel.screens.about.AboutScreenUiEvent.Consumed
import org.rhasspy.mobile.viewmodel.screens.about.AboutScreenUiEvent.Consumed.ShowSnackBar
import org.rhasspy.mobile.viewmodel.utils.OpenLinkUtils

/**
 * For About screen that displays app information
 * Holds changelog text and action to open source code link
 */
@Stable
class AboutScreenViewModel(
    viewStateCreator: AboutScreenViewStateCreator
) : ViewModel(), KoinComponent {

    private val _viewState: MutableStateFlow<AboutScreenViewState> = viewStateCreator()
    val viewState = _viewState.readOnly

    fun onEvent(event: AboutScreenUiEvent) {
        when (event) {
            is Action -> onAction(event)
            is Consumed -> onConsumed(event)
        }
    }

    private fun onAction(action: Action) {
        when (action) {
            OpenSourceCode -> openSourceCode()
        }
    }

    private fun onConsumed(consumed: Consumed) {
        _viewState.update {
            when (consumed) {
                ShowSnackBar -> it.copy(snackBarText = null)
            }
        }
    }

    private fun openSourceCode() {
        if (!OpenLinkUtils.openLink(LinkType.SourceCode)) {
            _viewState.update {
                it.copy(snackBarText = MR.strings.linkOpenFailed.stable)
            }
        }
    }

}