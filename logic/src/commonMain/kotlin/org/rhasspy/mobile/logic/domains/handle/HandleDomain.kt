package org.rhasspy.mobile.logic.domains.handle

import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.*
import org.rhasspy.mobile.data.connection.HttpClientResult
import org.rhasspy.mobile.data.domain.HandleDomainData
import org.rhasspy.mobile.data.service.option.HandleDomainOption
import org.rhasspy.mobile.data.service.option.HomeAssistantIntentHandlingOption
import org.rhasspy.mobile.logic.IDomain
import org.rhasspy.mobile.logic.connections.homeassistant.IHomeAssistantConnection
import org.rhasspy.mobile.logic.connections.mqtt.IMqttConnection
import org.rhasspy.mobile.logic.connections.mqtt.MqttConnectionEvent.EndSession
import org.rhasspy.mobile.logic.connections.mqtt.MqttConnectionEvent.Say
import org.rhasspy.mobile.logic.connections.webserver.IWebServerConnection
import org.rhasspy.mobile.logic.connections.webserver.WebServerConnectionEvent.WebServerSay
import org.rhasspy.mobile.logic.domains.IDomainHistory
import org.rhasspy.mobile.logic.local.indication.IIndication
import org.rhasspy.mobile.logic.pipeline.HandleResult
import org.rhasspy.mobile.logic.pipeline.HandleResult.Handle
import org.rhasspy.mobile.logic.pipeline.HandleResult.HandleError
import org.rhasspy.mobile.logic.pipeline.IntentResult.Intent
import org.rhasspy.mobile.logic.pipeline.Source.*
import org.rhasspy.mobile.logic.pipeline.domain.Reason
import org.rhasspy.mobile.platformspecific.timeoutWithDefault
import org.rhasspy.mobile.settings.ConfigurationSetting

/**
 * HandleDomain handles an intent using the defined option
 */
internal interface IHandleDomain : IDomain {

    /**
     * sends Intent and waits for an HandleResult result, normally text that is to be spoken
     */
    suspend fun awaitIntentHandle(intent: Intent): HandleResult

}

/**
 * calls actions and returns result
 *
 * when data is null the service was most probably mqtt and will return result in a call function
 */
internal class HandleDomain(
    private val params: HandleDomainData,
    private val mqttConnection: IMqttConnection,
    private val homeAssistantConnection: IHomeAssistantConnection,
    private val webServerConnection: IWebServerConnection,
    private val indication: IIndication,
    private val domainHistory: IDomainHistory,
) : IHandleDomain {

    private val logger = Logger.withTag("HandleDomain")

    /**
     * sends Intent and waits for an HandleResult result, normally text that is to be spoken
     */
    override suspend fun awaitIntentHandle(intent: Intent): HandleResult {
        logger.d { "awaitIntentHandle for intent $intent" }
        indication.onThinking()

        return when (params.option) {
            HandleDomainOption.HomeAssistant -> awaitHomeAssistantHandle(intent)
            HandleDomainOption.Disabled      ->
                HandleError(
                    sessionId = intent.sessionId,
                    reason = Reason.Disabled,
                    source = Local,
                )
        }.also {
            domainHistory.addToHistory(intent, it)
        }
    }

    /**
     * awaits for HomeAssistant to handle the intent
     */
    private suspend fun awaitHomeAssistantHandle(intent: Intent): HandleResult {
        return when (params.homeAssistantIntentHandlingOption) {
            HomeAssistantIntentHandlingOption.Event -> awaitHomeAssistantEventHandle(intent)
            HomeAssistantIntentHandlingOption.Intent -> awaitHomeAssistantIntentHandle(intent)
        }
    }

    /**
     * awaits for HomeAssistant to handle the intent on intent endpoint and reads text to be spoken from result
     */
    private suspend fun awaitHomeAssistantIntentHandle(intent: Intent): HandleResult {
        logger.d { "awaitHomeAssistantIntentHandle for intent $intent" }
        return when (val result = homeAssistantConnection.awaitIntent(intent.intentName, intent.intent)) {
            is HttpClientResult.HttpClientError ->
                HandleError(
                    sessionId = intent.sessionId,
                    reason = Reason.Error(result.message),
                    source = HomeAssistant,
                )

            is HttpClientResult.Success         ->
                Handle(
                    sessionId = intent.sessionId,
                    text = result.data,
                    volume = null,
                    source = HomeAssistant
                )
        }
    }

    /**
     * awaits for HomeAssistant to handle the intent on event endpoint, awaits end session or say from mqtt or say from webserver
     */
    private suspend fun awaitHomeAssistantEventHandle(intent: Intent): HandleResult {
        logger.d { "awaitHomeAssistantEventHandle for intent $intent" }
        homeAssistantConnection.awaitEvent(intent.intentName, intent.intent)

        //await for EndSession or Say
        return merge(
            mqttConnection.incomingMessages
                .filterIsInstance<EndSession>()
                .filter { it.sessionId == intent.sessionId }
                .map {
                    Handle(
                        sessionId = intent.sessionId,
                        text = it.text,
                        volume = null,
                        source = Rhasspy2HermesMqtt,
                    )
                },
            mqttConnection.incomingMessages
                .filterIsInstance<Say>()
                .filter { it.sessionId == intent.sessionId }
                .filter { it.siteId == ConfigurationSetting.siteId.value }
                .map {
                    Handle(
                        sessionId = intent.sessionId,
                        text = it.text,
                        volume = it.volume,
                        source = Rhasspy2HermesMqtt,
                    )
                },
            webServerConnection.incomingMessages
                .filterIsInstance<WebServerSay>()
                .map {
                    Handle(
                        sessionId = intent.sessionId,
                        text = it.text,
                        volume = null,
                        source = WebServer,
                    )
                },
        ).timeoutWithDefault(
            timeout = params.homeAssistantEventTimeout,
            default = HandleError(
                sessionId = intent.sessionId,
                reason = Reason.Timeout,
                source = Local,
            ),
        ).first()
    }

    override fun dispose() {}

}