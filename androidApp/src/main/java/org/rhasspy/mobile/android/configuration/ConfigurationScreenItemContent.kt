package org.rhasspy.mobile.android.configuration

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.material3.*
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.icerock.moko.resources.StringResource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.main.LocalMainNavController
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.android.utils.Icon
import org.rhasspy.mobile.android.utils.Text


/**
 * Content of Configuration Screen Item
 *
 * AppBar with Back button and title
 * BottomBar with Save, Discard actions and test FAB
 *
 * Shows dialog on Back press when there are unsaved changes
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ConfigurationScreenItemContent(
    modifier: Modifier,
    title: StringResource,
    hasUnsavedChanges: StateFlow<Boolean>,
    testingEnabled: StateFlow<Boolean> = MutableStateFlow(true),
    onSave: () -> Unit,
    onTest: () -> Unit,
    onDiscard: () -> Unit,
    Content: @Composable ColumnScope.(onNavigate: (route: String) -> Unit) -> Unit
) {

    val navController = LocalMainNavController.current
    val modalBottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()

    var showBackButtonDialog by rememberSaveable { mutableStateOf(false) }
    var showNavigateDialog by rememberSaveable { mutableStateOf(false) }
    var navigationRoute by rememberSaveable { mutableStateOf("") }

    fun onBackPress() {
        if (hasUnsavedChanges.value) {
            showBackButtonDialog = true
        } else {
            navController.popBackStack()
        }
    }

    fun onNavigate(route: String) {
        if (hasUnsavedChanges.value) {
            navigationRoute = route
            showNavigateDialog = true
        } else {
            navController.navigateSingle(route)
        }
    }

    //Back handler to show dialog if there are unsaved changes
    BackHandler(onBack = { onBackPress() })

    //Show unsaved changes dialog back press
    if (showBackButtonDialog) {
        UnsavedBackButtonDialog(
            onSave = onSave,
            onDiscard = onDiscard,
            onClose = {
                showBackButtonDialog = false
            }
        )
    }

    //Show unsaved changes dialog navigate
    if (showNavigateDialog) {
        UnsavedNavigationButtonDialog(
            route = navigationRoute,
            onSave = onSave,
            onClose = {
                showNavigateDialog = false
            }
        )
    }

    //appbar, bottomAppBar, content

    Surface(tonalElevation = 1.dp) {
        ModalBottomSheetLayout(
            sheetBackgroundColor = Color.Transparent,
            sheetState = modalBottomSheetState,
            sheetContent = { BottomSheet() })
        {
            Scaffold(
                modifier = modifier
                    .fillMaxSize()
                    .testTag(TestTag.ConfigurationScreenItemContent),
                topBar = {
                    AppBar(
                        title = title,
                        onBackClick = { onBackPress() }
                    )
                },
                bottomBar = {
                    BottomAppBar(
                        hasUnsavedChanges = hasUnsavedChanges,
                        testingEnabled = testingEnabled,
                        onSave = onSave,
                        onClick = {
                            scope.launch {
                                modalBottomSheetState.show()
                            }
                        },
                        onDiscard = onDiscard
                    )
                }
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    Content { route -> onNavigate(route) }
                }
            }
        }
    }
}


private fun NavController.navigateSingle(route: String) {
    if (this.backQueue.lastOrNull { entry -> entry.destination.route == route } != null) {
        this.popBackStack(
            route = route,
            inclusive = false
        )
    } else {
        this.navigate(route)
    }
}


@Composable
private fun UnsavedBackButtonDialog(onSave: () -> Unit, onDiscard: () -> Unit, onClose: () -> Unit) {
    val navController = LocalMainNavController.current

    UnsavedChangesDialog(
        onDismissRequest = onClose,
        onSave = {
            onSave.invoke()
            navController.popBackStack()
            onClose.invoke()
        },
        onDiscard = {
            onDiscard.invoke()
            navController.popBackStack()
            onClose.invoke()
        },
        dismissButtonText = MR.strings.discard
    )
}

@Composable
private fun UnsavedNavigationButtonDialog(route: String, onSave: () -> Unit, onClose: () -> Unit) {
    val navController = LocalMainNavController.current

    UnsavedChangesDialog(
        onDismissRequest = onClose,
        onSave = {
            onSave.invoke()
            navController.navigateSingle(route)
            onClose.invoke()
        },
        onDiscard = onClose,
        dismissButtonText = MR.strings.cancel
    )

}

/**
 * Dialog to be shown when there are unsaved changes
 * save changes or undo changes and go back
 */
@Composable
private fun UnsavedChangesDialog(
    onDismissRequest: () -> Unit,
    onSave: () -> Unit,
    onDiscard: () -> Unit,
    dismissButtonText: StringResource
) {

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = onSave,
                modifier = Modifier.testTag(TestTag.DialogOk)
            ) {
                Text(MR.strings.save)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDiscard,
                modifier = Modifier.testTag(TestTag.DialogCancel)
            ) {
                Text(dismissButtonText)
            }
        },
        icon = {
            Icon(
                imageVector = Icons.Filled.Warning,
                contentDescription = MR.strings.discard
            )
        },
        title = { Text(MR.strings.unsavedChanges) },
        text = { Text(MR.strings.unsavedChangesInformation) },
        modifier = Modifier.testTag(TestTag.DialogUnsavedChanges)
    )

}

/**
 * bottom app bar
 * discard, save actions
 * fab for testing
 */
@Composable
private fun BottomAppBar(
    hasUnsavedChanges: StateFlow<Boolean>,
    testingEnabled: StateFlow<Boolean>,
    onSave: () -> Unit,
    onClick: () -> Unit,
    onDiscard: () -> Unit
) {
    val isHasUnsavedChanges by hasUnsavedChanges.collectAsState()
    val isTestingEnabled by testingEnabled.collectAsState()

    BottomAppBar(
        actions = {
            IconButton(
                modifier = Modifier.testTag(TestTag.BottomAppBarDiscard),
                onClick = onDiscard,
                enabled = isHasUnsavedChanges
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = MR.strings.discard,
                )
            }
            IconButton(
                modifier = Modifier.testTag(TestTag.BottomAppBarSave),
                onClick = onSave,
                enabled = isHasUnsavedChanges
            ) {
                Icon(
                    imageVector = Icons.Filled.Save,
                    contentDescription = MR.strings.save
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.testTag(TestTag.BottomAppBarTest),
                onClick = {},
                containerColor = Color.Transparent,
                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
            ) {
                val contentColor = LocalContentColor.current

                CompositionLocalProvider(
                    LocalRippleTheme provides if (isTestingEnabled) LocalRippleTheme.current else NoRippleTheme
                ) {
                    Button(
                        modifier = Modifier.defaultMinSize(
                            minWidth = 56.0.dp,
                            minHeight = 56.0.dp,
                        ),
                        shape = RoundedCornerShape(16.0.dp),
                        colors = ButtonDefaults.buttonColors(
                            contentColor = contentColor,
                            containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                            disabledContainerColor = BottomAppBarDefaults.bottomAppBarFabColor.copy(alpha = 0.12f)
                        ),
                        onClick = onClick,
                        enabled = isTestingEnabled
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = MR.strings.test
                        )
                    }
                }
            }
        }
    )
}

private object NoRippleTheme : RippleTheme {
    @Composable
    override fun defaultColor() = Color.Unspecified

    @Composable
    override fun rippleAlpha(): RippleAlpha = RippleAlpha(
        draggedAlpha = 0f,
        focusedAlpha = 0f,
        hoveredAlpha = 0f,
        pressedAlpha = 0f,
    )
}

/**
 * top app bar with title and back navigation button
 */
@Composable
private fun AppBar(title: StringResource, onBackClick: () -> Unit) {

    TopAppBar(
        title = {
            Text(
                resource = title,
                modifier = Modifier.testTag(TestTag.AppBarTitle)
            )
        },
        navigationIcon = {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.testTag(TestTag.AppBarBackButton)
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = MR.strings.back,
                )
            }
        }
    )
}

@Composable
private fun BottomSheet() {
    Surface(
        tonalElevation = 2.dp,
        shape = RoundedCornerShape(28.dp, 28.dp)) {
        Column(
            modifier = Modifier
                .widthIn(max = 640.dp)
                .fillMaxWidth()
                .padding(8.dp),
        ) {
            Box(modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(50),
                    )
                    .size(width = 32.dp, height = 4.dp)
                    .padding(vertical = 22.dp)
                    .align(Alignment.CenterHorizontally),
            )
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}
