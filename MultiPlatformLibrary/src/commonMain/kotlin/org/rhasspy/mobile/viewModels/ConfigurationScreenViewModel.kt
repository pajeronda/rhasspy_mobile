package org.rhasspy.mobile.viewModels

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import org.rhasspy.mobile.services.MqttService
import org.rhasspy.mobile.settings.ConfigurationSettings

class ConfigurationScreenViewModel : ViewModel() {

    val siteId = ConfigurationSettings.siteId.data
    val isHttpServerEnabled = ConfigurationSettings.isHttpServerEnabled.data
    val isHttpSSLVerificationEnabled = ConfigurationSettings.isHttpSSLVerificationDisabled.data
    val isMQTTConnected = MqttService.isConnected
    val isUdpOutputEnabled = ConfigurationSettings.isUdpOutputEnabled.data
    val wakeWordOption = ConfigurationSettings.wakeWordOption.data
    val speechToTextOption = ConfigurationSettings.speechToTextOption.data
    val intentRecognitionOption = ConfigurationSettings.intentRecognitionOption.data
    val textToSpeechOption = ConfigurationSettings.textToSpeechOption.data
    val audioPlayingOption = ConfigurationSettings.audioPlayingOption.data
    val dialogManagementOption = ConfigurationSettings.dialogManagementOption.data
    val intentHandlingOption = ConfigurationSettings.intentHandlingOption.data

    fun changeSiteId(siteId: String) {
        ConfigurationSettings.siteId.value = siteId
    }

    init{
        println("init")
    }

}