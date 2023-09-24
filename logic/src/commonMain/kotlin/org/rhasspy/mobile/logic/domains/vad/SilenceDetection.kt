package org.rhasspy.mobile.logic.domains.vad

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.rhasspy.mobile.logic.domains.mic.MicAudioChunk
import org.rhasspy.mobile.logic.pipeline.PipelineEvent.AudioDomainEvent.AudioChunkEvent

class SilenceDetection(
    private val automaticSilenceDetectionTime: Long?,
    private val automaticSilenceDetectionMinimumTime: Long?,
    private val automaticSilenceDetectionAudioLevel: Float,
) {

    private var silenceStartTime: Instant? = null
    private var recordingTillSilenceStartTime = Clock.System.now()

    //returns true when silence was detected
    fun onAudioChunk(chunk: MicAudioChunk): Boolean {
        return true /*
        val automaticSilenceDetectionTime = automaticSilenceDetectionTime ?: 0
        val automaticSilenceDetectionMinimumTime = automaticSilenceDetectionMinimumTime ?: 0

        //check minimum recording time
        val currentTime = Clock.System.now()
        //if recordingTillSilenceStartTime is null there is an issue therefore timeSinceStart is set to 0
        val timeSinceStart = currentTime.minus(recordingTillSilenceStartTime)

        //minimum recording time not reached
        if (timeSinceStart < automaticSilenceDetectionMinimumTime.milliseconds) return false
/*
        var max: Short = 0
        for (i in 0..chunk.data.size step 2) {
            if (i < chunk.data.size) {
                val bb = ByteBuffer.wrap(chunk.data.copyOfRange(i, i + 2).nat)
                bb.order(ByteOrder.nativeOrder())
                val short = bb.short

                if (short > max) {
                    max = short
                }
            }
        }

        //volume above threshold
        if (volume > automaticSilenceDetectionAudioLevel) {
            //reset silence time (was above threshold)
            silenceStartTime = null
            return
        }*/

        //volume below threshold

        //not initial silence
        silenceStartTime?.also { silenceStart ->
            //silence duration
            val timeSinceSilenceDetected = currentTime.minus(silenceStart)
            //check if silence was detected for x milliseconds
            if (timeSinceSilenceDetected >= automaticSilenceDetectionTime.milliseconds) {
            //    onSilenceDetected()
            }
        } ?: run {
            //first time silence was detected
            silenceStartTime = Clock.System.now()
        }
*/
    }

}