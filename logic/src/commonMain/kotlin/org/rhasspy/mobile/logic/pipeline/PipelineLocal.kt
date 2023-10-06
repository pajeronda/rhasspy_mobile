package org.rhasspy.mobile.logic.pipeline

import com.benasher44.uuid.uuid4
import org.rhasspy.mobile.data.audiofocus.AudioFocusRequestReason.Record
import org.rhasspy.mobile.logic.local.audiofocus.IAudioFocus
import org.rhasspy.mobile.logic.pipeline.HandleResult.*
import org.rhasspy.mobile.logic.pipeline.IntentResult.*
import org.rhasspy.mobile.logic.pipeline.SndResult.*
import org.rhasspy.mobile.logic.pipeline.TranscriptResult.*
import org.rhasspy.mobile.logic.pipeline.TtsResult.*
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.settings.ConfigurationSetting

internal interface IPipelineLocal : IPipeline

internal class PipelineLocal(
    private val domains: DomainBundle,
    private val audioFocus: IAudioFocus,
) : IPipelineLocal {

    override suspend fun runPipeline(startEvent: StartEvent): PipelineResult {

        //use session id from event or create own one
        val sessionId = startEvent.sessionId ?: uuid4().toString()

        //transcript audio to text from voice start till voice stop
        val transcript = when (
            val result = domains.asrDomain.awaitTranscript(
                sessionId = sessionId,
                audioStream = domains.micDomain.audioStream,
                awaitVoiceStart = domains.vadDomain::awaitVoiceStart,
                awaitVoiceStopped = domains.vadDomain::awaitVoiceStopped,
            ).also {
                audioFocus.abandon(Record)
            }
        ) {
            is Transcript         -> result
            is TranscriptError    -> return result
            is TranscriptDisabled -> return result
            is TranscriptTimeout  -> return result
        }

        //find intent from text, eventually already handles
        val intent = domains.intentDomain.awaitIntent(
            sessionId = sessionId,
            transcript = transcript
        )

        //handle intent
        val handle = when (intent) {
            is Handle         -> intent
            is NotHandled     -> return intent
            is Intent         -> {
                when (
                    val result = domains.handleDomain.awaitIntentHandle(
                        sessionId = sessionId,
                        intent = intent
                    )
                ) {
                    is Handle         -> result
                    is NotHandled     -> return result
                    is HandleDisabled -> return result
                }
            }

            is NotRecognized  -> return intent
            is HandleDisabled -> return intent
            is IntentDisabled -> return intent
        }

        //translate handle text to speech
        val tts = when (val result = domains.ttsDomain.onSynthesize(
            sessionId = sessionId,
            volume = AppSetting.volume.value,
            siteId = ConfigurationSetting.siteId.value,
            handle = handle,
        )) {
            is Audio          -> result
            is NotSynthesized -> return result
            is Played         -> return result
            is TtsDisabled    -> return result
        }

        //play audio
        return when (val result = domains.sndDomain.awaitPlayAudio(tts)) {
            is Played       -> result
            is NotPlayed    -> result
            is PlayDisabled -> result
        }.also {
            domains.dispose()
        }
    }

}