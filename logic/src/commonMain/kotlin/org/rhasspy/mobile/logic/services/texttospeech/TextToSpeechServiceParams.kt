package org.rhasspy.mobile.logic.services.texttospeech

import org.rhasspy.mobile.logic.settings.ConfigurationSetting
import org.rhasspy.mobile.data.service.option.TextToSpeechOption

data class TextToSpeechServiceParams(
    val textToSpeechOption: TextToSpeechOption = ConfigurationSetting.textToSpeechOption.value
)