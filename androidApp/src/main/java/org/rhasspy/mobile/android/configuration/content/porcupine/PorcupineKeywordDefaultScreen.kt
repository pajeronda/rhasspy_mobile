package org.rhasspy.mobile.android.configuration.content.porcupine

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.combinedTestTag
import org.rhasspy.mobile.android.content.elements.CustomDivider
import org.rhasspy.mobile.android.content.elements.Text
import org.rhasspy.mobile.android.content.list.ListElement
import org.rhasspy.mobile.android.content.list.SliderListItem
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.data.porcupine.PorcupineDefaultKeyword
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.PorcupineUiEvent
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.PorcupineUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationViewState.PorcupineViewState

/**
 * default keywords screen
 */
@Composable
fun PorcupineKeywordDefaultScreen(
    viewState: PorcupineViewState,
    onEvent: (PorcupineUiEvent) -> Unit
) {

    LazyColumn(
        modifier = Modifier
            .testTag(TestTag.PorcupineKeywordDefaultScreen)
            .fillMaxHeight()
    ) {
        items(viewState.defaultOptionsUi) { option ->

            DefaultKeywordListItem(
                element = option,
                onClick = { onEvent(ClickPorcupineKeywordDefault(option)) },
                onToggle = { onEvent(SetPorcupineKeywordDefault(option, it)) },
                onUpdateSensitivity = { onEvent(UpdateWakeWordPorcupineKeywordDefaultSensitivity(option, it)) }
            )

            CustomDivider()
        }
    }

}

/**
 * list item for default keywords
 * enabled/disabled
 * sensitivity
 */
@Composable
private fun DefaultKeywordListItem(
    element: PorcupineDefaultKeyword,
    onClick: () -> Unit,
    onToggle: (enabled: Boolean) -> Unit,
    onUpdateSensitivity: (sensitivity: Float) -> Unit,
) {
    ListElement(
        modifier = Modifier
            .testTag(IOption = element.option)
            .clickable(onClick = onClick),
        icon = {
            Checkbox(
                checked = element.isEnabled,
                onCheckedChange = onToggle
            )
        },
        text = {
            Text(element.option.text)
        }
    )

    if (element.isEnabled) {
        //sensitivity of porcupine
        SliderListItem(
            modifier = Modifier
                .combinedTestTag(element.option, TestTag.Sensitivity)
                .padding(horizontal = 12.dp),
            text = MR.strings.sensitivity.stable,
            value = element.sensitivity,
            onValueChange = onUpdateSensitivity
        )
    }
}