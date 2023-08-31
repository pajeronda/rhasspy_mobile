package org.rhasspy.mobile.viewmodel.configuration.connections.http.detail

import androidx.compose.runtime.Stable
import org.rhasspy.mobile.platformspecific.toStringOrEmpty
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewState
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewState.IConfigurationData

@Stable
data class HttpConnectionDetailConfigurationViewState internal constructor(
    override val editData: RemoteHermesHttpConfigurationData
) : IConfigurationViewState {

    @Stable
    data class RemoteHermesHttpConfigurationData internal constructor(
        val httpClientServerEndpointHost: String = "",//TODO ConfigurationSetting.httpClientServerEndpointHost.value,
        val httpClientServerEndpointPort: Int? = null,//TODO ConfigurationSetting.httpClientServerEndpointPort.value,
        val httpClientTimeout: Long? = null,//TODO ConfigurationSetting.httpClientTimeout.value,
        val isHttpSSLVerificationDisabled: Boolean = true,//TODO ConfigurationSetting.isHttpClientSSLVerificationDisabled.value,
        val isRhasspy2Hermes: Boolean = false,
        val isRhasspy3Wyoming: Boolean = false,
        val isHomeAssistant: Boolean = false,
    ) : IConfigurationData {

        val httpClientServerEndpointPortText: String = httpClientServerEndpointPort.toStringOrEmpty()
        val httpClientTimeoutText: String = httpClientTimeout.toStringOrEmpty()

    }

}