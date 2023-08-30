package org.rhasspy.mobile.viewmodel.configuration.connections.http.list

sealed interface HttpConnectionListConfigurationUiEvent {

    sealed interface Action : HttpConnectionListConfigurationUiEvent {

        data object BackClick : Action
        data object AddClick : Action
        data class ItemClick(val id: Int) : Action
        data class ItemDeleteClick(val id: Int) : Action

    }

}