package org.rhasspy.mobile.data.audiorecorder

import kotlinx.serialization.Serializable
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.service.option.IOption
import org.rhasspy.mobile.resources.MR

@Serializable
actual enum class AudioFormatSampleRateType(
    override val text: StableStringResource,
    actual val value: Int
) : IOption {
    //TODO #509
    Default(MR.strings.defaultText.stable, 1);

    actual companion object {
        actual val default: AudioFormatSampleRateType get() = Default
    }
}