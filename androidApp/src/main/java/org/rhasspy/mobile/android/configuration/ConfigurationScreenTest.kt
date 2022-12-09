package org.rhasspy.mobile.android.configuration

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.StringResource
import kotlinx.coroutines.launch
import org.rhasspy.mobile.Application
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.content.elements.EventListItem
import org.rhasspy.mobile.android.content.elements.Icon
import org.rhasspy.mobile.android.content.elements.Text
import org.rhasspy.mobile.android.content.item.EventStateCard
import org.rhasspy.mobile.android.content.item.EventStateIcon
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.middleware.EventState
import org.rhasspy.mobile.viewModels.configuration.IConfigurationViewModel

@Composable
fun ConfigurationScreenTest(
    viewModel: IConfigurationViewModel,
    content: (@Composable () -> Unit)?,
    onOpenPage: () -> Unit
) {

    val navController = LocalConfigurationNavController.current

    LaunchedEffect(Unit) {
        onOpenPage.invoke()
        Application.Instance.isAppInBackground.collect {
            if (it) {
                navController.popBackStack()
            }
        }
    }

    Scaffold(
        topBar = {
            AppBar(
                viewModel = viewModel,
                title = MR.strings.test,
                onBackClick = navController::popBackStack
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = MR.strings.stop,
                )
            }
        },
    ) { paddingValues ->
        Surface(
            modifier = Modifier.padding(paddingValues),
            tonalElevation = 3.dp
        ) {
            ConfigurationScreenTestList(
                viewModel = viewModel,
                content = content
            )
        }
    }
}

/**
 * list and custom content
 */
@Composable
private fun ConfigurationScreenTestList(
    modifier: Modifier = Modifier,
    viewModel: IConfigurationViewModel,
    content: (@Composable () -> Unit)?
) {
    Column(modifier = modifier) {
        val eventsList by viewModel.events.collectAsState()
        val coroutineScope = rememberCoroutineScope()
        val scrollState = rememberLazyListState()

        LaunchedEffect(eventsList.size) {
            coroutineScope.launch {
                if (eventsList.isNotEmpty()) {
                    scrollState.animateScrollToItem(eventsList.size - 1)
                }
            }
        }

        LazyColumn(
            state = scrollState,
            modifier = Modifier.weight(1f)
        ) {
            stickyHeader {
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp))
                        .padding(16.dp)
                        .fillMaxWidth(),
                ) {
                    ServiceState(viewModel.serviceState.collectAsState().value)
                }
            }

            items(eventsList) { item ->
                EventListItem(item)
            }
        }

        if (content != null) {
            Card(
                modifier = Modifier.padding(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                content()
            }
        }

    }
}

/**
 * top app bar with title and back navigation button
 *
 * actions: autoscroll, expand and filter
 */
@Composable
private fun AppBar(
    viewModel: IConfigurationViewModel,
    title: StringResource,
    onBackClick: () -> Unit,
    icon: @Composable () -> Unit
) {
    TopAppBar(
        colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)),
        title = {
            Text(
                resource = title,
                modifier = Modifier.testTag(TestTag.AppBarTitle)
            )
        },
        navigationIcon = {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.testTag(TestTag.AppBarBackButton),
                content = icon
            )
        },
        actions = {
            IconButton(onClick = viewModel::toggleListExpanded) {
                Icon(
                    imageVector = if (viewModel.isListExpanded.collectAsState().value) Icons.Filled.Compress else Icons.Filled.Expand,
                    contentDescription = MR.strings.share
                )
            }
            IconButton(onClick = viewModel::toggleListFiltered) {
                Icon(
                    imageVector = if (viewModel.isListFiltered.collectAsState().value) Icons.Filled.FilterListOff else Icons.Filled.FilterList,
                    contentDescription = MR.strings.share
                ) //FilterListOff
            }
            IconButton(onClick = viewModel::toggleListAutoscroll) {
                Icon(
                    imageVector = if (viewModel.isListAutoscroll.collectAsState().value) Icons.Filled.LowPriority else Icons.Filled.PlaylistRemove,
                    contentDescription = MR.strings.share
                ) //LowPriority
            }
        }
    )
}

@Composable
fun ServiceState(serviceState: EventState) {

    EventStateCard(
        eventState = serviceState,
        onClick = null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            EventStateIcon(serviceState)
            ServiceStateText(serviceState)
        }
    }
}

@Composable
private fun ServiceStateText(serviceState: EventState) {

    Text(
        resource = when (serviceState) {
            is EventState.Pending -> MR.strings.pending
            is EventState.Loading -> MR.strings.loading
            is EventState.Success -> MR.strings.success
            is EventState.Warning -> MR.strings.warning
            is EventState.Error -> MR.strings.error
            is EventState.Disabled -> MR.strings.disabled
        }
    )

}