package org.rhasspy.mobile.logic.connections.httpclient

import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.platformspecific.mapReadonlyState
import org.rhasspy.mobile.settings.ConfigurationSetting

internal class HttpClientServiceParamsCreator {

    operator fun invoke(): StateFlow<HttpClientServiceParams> {

        return combineStateFlow(
            ConfigurationSetting.siteId.data,
            //TODO ConfigurationSetting.isHttpClientSSLVerificationDisabled.data,
            //TODO ConfigurationSetting.httpClientServerEndpointHost.data,
            //TODO ConfigurationSetting.httpClientServerEndpointPort.data,
            //TODO ConfigurationSetting.httpClientTimeout.data,
            ConfigurationSetting.isUseCustomSpeechToTextHttpEndpoint.data,
            ConfigurationSetting.speechToTextHttpEndpoint.data,
            ConfigurationSetting.isUseCustomIntentRecognitionHttpEndpoint.data,
            ConfigurationSetting.intentRecognitionHttpEndpoint.data,
            ConfigurationSetting.isUseCustomTextToSpeechHttpEndpoint.data,
            ConfigurationSetting.textToSpeechHttpEndpoint.data,
            ConfigurationSetting.isUseCustomAudioPlayingHttpEndpoint.data,
            ConfigurationSetting.audioPlayingHttpEndpoint.data,
            ConfigurationSetting.intentHandlingHttpEndpoint.data,
            ConfigurationSetting.intentHandlingHomeAssistantEndpoint.data,
            ConfigurationSetting.intentHandlingHomeAssistantAccessToken.data,
            ConfigurationSetting.intentHandlingOption.data
        ).mapReadonlyState {
            getParams()
        }

    }

    private fun getParams(): HttpClientServiceParams {
        return HttpClientServiceParams(
            siteId = ConfigurationSetting.siteId.value,
            isHttpSSLVerificationDisabled = false,//TODO ConfigurationSetting.isHttpClientSSLVerificationDisabled.value,
            httpClientServerEndpointHost = "",//TODO ConfigurationSetting.httpClientServerEndpointHost.value,
            httpClientServerEndpointPort = 1,//TODO ConfigurationSetting.httpClientServerEndpointPort.value,
            httpClientTimeout = 5,//TODO ConfigurationSetting.httpClientTimeout.value,
            isUseCustomSpeechToTextHttpEndpoint = ConfigurationSetting.isUseCustomSpeechToTextHttpEndpoint.value,
            speechToTextHttpEndpoint = ConfigurationSetting.speechToTextHttpEndpoint.value,
            isUseCustomIntentRecognitionHttpEndpoint = ConfigurationSetting.isUseCustomIntentRecognitionHttpEndpoint.value,
            intentRecognitionHttpEndpoint = ConfigurationSetting.intentRecognitionHttpEndpoint.value,
            isUseCustomTextToSpeechHttpEndpoint = ConfigurationSetting.isUseCustomTextToSpeechHttpEndpoint.value,
            textToSpeechHttpEndpoint = ConfigurationSetting.textToSpeechHttpEndpoint.value,
            isUseCustomAudioPlayingEndpoint = ConfigurationSetting.isUseCustomAudioPlayingHttpEndpoint.value,
            audioPlayingHttpEndpoint = ConfigurationSetting.audioPlayingHttpEndpoint.value,
            intentHandlingHttpEndpoint = ConfigurationSetting.intentHandlingHttpEndpoint.value,
            intentHandlingHomeAssistantEndpoint = ConfigurationSetting.intentHandlingHomeAssistantEndpoint.value,
            intentHandlingHomeAssistantAccessToken = ConfigurationSetting.intentHandlingHomeAssistantAccessToken.value,
            intentHandlingOption = ConfigurationSetting.intentHandlingOption.value
        )
    }


}