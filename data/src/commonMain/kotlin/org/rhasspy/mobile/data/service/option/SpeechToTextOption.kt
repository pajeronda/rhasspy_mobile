package org.rhasspy.mobile.data.service.option

import dev.icerock.moko.resources.StringResource
import org.rhasspy.mobile.MR

enum class SpeechToTextOption(override val text: StringResource) : IOption<SpeechToTextOption> {
    RemoteHTTP(MR.strings.remoteHTTP),
    RemoteMQTT(MR.strings.remoteMQTT),
    Disabled(MR.strings.disabled);

    override fun findValue(value: String): SpeechToTextOption {
        return valueOf(value)
    }
}