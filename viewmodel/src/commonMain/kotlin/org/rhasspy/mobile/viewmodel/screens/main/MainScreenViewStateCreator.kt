package org.rhasspy.mobile.viewmodel.screens.main

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.platformspecific.utils.isDebug
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.viewmodel.navigation.Navigator
import org.rhasspy.mobile.viewmodel.navigation.destinations.MainScreenNavigationDestination
import org.rhasspy.mobile.viewmodel.navigation.destinations.MainScreenNavigationDestination.*

class MainScreenViewStateCreator(
    private val navigator: Navigator
) {

    private val updaterScope = CoroutineScope(Dispatchers.IO)

    private val mainScreens = arrayOf(HomeScreen, ConfigurationScreen, SettingsScreen, LogScreen)

    operator fun invoke(): StateFlow<MainScreenViewState> {
        val viewState = MutableStateFlow(getViewState())

        updaterScope.launch {
            combineStateFlow(
                navigator.navStack,
                AppSetting.isShowLogEnabled.data,
                AppSetting.didShowCrashlyticsDialog.data
            ).collect {
                viewState.value = getViewState()
            }
        }

        return viewState
    }

    private fun getViewState(): MainScreenViewState {
        return MainScreenViewState(
            isBottomNavigationVisible = navigator.navStack.value.lastOrNull() is MainScreenNavigationDestination,
            bottomNavigationIndex = mainScreens.indexOf(navigator.topScreen<MainScreenNavigationDestination>().value),
            isShowLogEnabled = AppSetting.isShowLogEnabled.value,
            isShowCrashlyticsDialog = !AppSetting.didShowCrashlyticsDialog.value && !isDebug()
        )
    }
}