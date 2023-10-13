package org.rhasspy.mobile.logic

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.rhasspy.mobile.logic.connections.homeassistant.HomeAssistantConnection
import org.rhasspy.mobile.logic.connections.homeassistant.IHomeAssistantConnection
import org.rhasspy.mobile.logic.connections.mqtt.IMqttConnection
import org.rhasspy.mobile.logic.connections.mqtt.MqttConnection
import org.rhasspy.mobile.logic.connections.mqtt.MqttConnectionParamsCreator
import org.rhasspy.mobile.logic.connections.rhasspy2hermes.IRhasspy2HermesConnection
import org.rhasspy.mobile.logic.connections.rhasspy2hermes.Rhasspy2HermesConnection
import org.rhasspy.mobile.logic.connections.rhasspy3wyoming.IRhasspy3WyomingConnection
import org.rhasspy.mobile.logic.connections.rhasspy3wyoming.Rhasspy3WyomingConnection
import org.rhasspy.mobile.logic.connections.user.IUserConnection
import org.rhasspy.mobile.logic.connections.user.UserConnection
import org.rhasspy.mobile.logic.connections.webserver.IWebServerConnection
import org.rhasspy.mobile.logic.connections.webserver.WebServerConnection
import org.rhasspy.mobile.logic.domains.DomainHistory
import org.rhasspy.mobile.logic.domains.IDomainHistory
import org.rhasspy.mobile.logic.domains.asr.AsrDomain
import org.rhasspy.mobile.logic.domains.asr.IAsrDomain
import org.rhasspy.mobile.logic.domains.audio.AudioDomain
import org.rhasspy.mobile.logic.domains.audio.IAudioDomain
import org.rhasspy.mobile.logic.domains.handle.HandleDomain
import org.rhasspy.mobile.logic.domains.handle.IHandleDomain
import org.rhasspy.mobile.logic.domains.intent.IIntentDomain
import org.rhasspy.mobile.logic.domains.intent.IntentDomain
import org.rhasspy.mobile.logic.domains.mic.IMicDomain
import org.rhasspy.mobile.logic.domains.mic.MicDomain
import org.rhasspy.mobile.logic.domains.snd.ISndDomain
import org.rhasspy.mobile.logic.domains.snd.SndDomain
import org.rhasspy.mobile.logic.domains.tts.ITtsDomain
import org.rhasspy.mobile.logic.domains.tts.TtsDomain
import org.rhasspy.mobile.logic.domains.vad.IVadDomain
import org.rhasspy.mobile.logic.domains.vad.VadDomain
import org.rhasspy.mobile.logic.domains.wake.IWakeDomain
import org.rhasspy.mobile.logic.domains.wake.UdpConnection
import org.rhasspy.mobile.logic.domains.wake.WakeDomain
import org.rhasspy.mobile.logic.local.audiofocus.AudioFocus
import org.rhasspy.mobile.logic.local.audiofocus.IAudioFocus
import org.rhasspy.mobile.logic.local.file.FilesStorage
import org.rhasspy.mobile.logic.local.file.IFileStorage
import org.rhasspy.mobile.logic.local.indication.IIndication
import org.rhasspy.mobile.logic.local.indication.Indication
import org.rhasspy.mobile.logic.local.localaudio.ILocalAudioPlayer
import org.rhasspy.mobile.logic.local.localaudio.LocalAudioPlayer
import org.rhasspy.mobile.logic.local.settings.AppSettingsUtil
import org.rhasspy.mobile.logic.local.settings.IAppSettingsUtil
import org.rhasspy.mobile.logic.logger.DatabaseLogger
import org.rhasspy.mobile.logic.logger.IDatabaseLogger
import org.rhasspy.mobile.logic.pipeline.DomainBundle
import org.rhasspy.mobile.logic.pipeline.IPipelineManager
import org.rhasspy.mobile.logic.pipeline.PipelineManager
import org.rhasspy.mobile.logic.pipeline.impls.PipelineDisabled
import org.rhasspy.mobile.logic.pipeline.impls.PipelineLocal
import org.rhasspy.mobile.logic.pipeline.impls.PipelineMqtt
import org.rhasspy.mobile.settings.ConfigurationSetting

fun logicModule() = module {
    factory {
        DomainBundle(
            wakeDomain = get(),
            audioDomain = get(),
            asrDomain = get(),
            handleDomain = get(),
            intentDomain = get(),
            micDomain = get(),
            sndDomain = get(),
            ttsDomain = get(),
            vadDomain = get(),
        )
    }

    single<IDomainHistory> {
        DomainHistory(
            mqttConnection = get()
        )
    }

    factory { params ->
        PipelineLocal(
            params = params[0],
            domains = params[1],
            domainHistory = get(),
            audioFocus = get(),
            localAudioPlayer = get(),
        )
    }
    factory { params ->
        PipelineMqtt(
            mqttConnection = get(),
            domains = params.get(),
            domainHistory = get(),
            audioFocus = get(),
        )
    }
    factory { params ->
        PipelineDisabled(
            domains = params.get(),
        )
    }

    single<IPipelineManager> {
        PipelineManager(
            indication = get(),
        )
    }

    factory<IMicDomain> {
        MicDomain(
            params = ConfigurationSetting.micDomainData.value,
            audioRecorder = get(),
            microphonePermission = get(),
        )
    }
    factory<ISndDomain> {
        SndDomain(
            params = ConfigurationSetting.sndDomainData.value,
            fileStorage = get(),
            audioFocusService = get(),
            localAudioService = get(),
            mqttConnection = get(),
            httpClientConnection = get(),
            domainHistory = get(),
            indication = get(),
        )
    }

    factory<IAudioDomain> {
        AudioDomain(
            mqttConnection = get(),
            webServerConnection = get(),
            userConnection = get(),
            fileStorage = get(),
        )
    }

    factory<IHandleDomain> {
        HandleDomain(
            params = ConfigurationSetting.handleDomainData.value,
            mqttConnection = get(),
            homeAssistantConnection = get(),
            webServerConnection = get(),
            indication = get(),
            domainHistory = get(),
        )
    }

    factory<IIntentDomain> {
        IntentDomain(
            params = ConfigurationSetting.intentDomainData.value,
            mqttConnection = get(),
            webServerConnection = get(),
            rhasspy2HermesConnection = get(),
            indication = get(),
            domainHistory = get(),
        )
    }
    factory<IAsrDomain> {
        AsrDomain(
            params = ConfigurationSetting.asrDomainData.value,
            mqttConnection = get(),
            rhasspy2HermesConnection = get(),
            indication = get(),
            fileStorage = get(),
            audioFocus = get(),
            domainHistory = get(),
        )
    }

    factory<ITtsDomain> {
        TtsDomain(
            params = ConfigurationSetting.ttsDomainData.value,
            mqttConnection = get(),
            rhasspy2HermesConnection = get(),
            domainHistory = get(),
        )
    }

    factory<IWakeDomain> {
        WakeDomain(
            params = ConfigurationSetting.wakeDomainData.value,
            mqttConnection = get(),
            webServerConnection = get(),
            userConnection = get(),
            domainHistory = get(),
        )
    }

    factory<IVadDomain> {
        VadDomain(
            params = ConfigurationSetting.vadDomainData.value,
            userConnection = get(),
            domainHistory = get(),
        )
    }

    single<IAudioFocus> { AudioFocus() }


    single<IIndication> {
        Indication()
    }

    single<ILocalAudioPlayer> {
        LocalAudioPlayer(
            audioFocusService = get(),
        )
    }

    single<IAppSettingsUtil> { AppSettingsUtil() }

    single<IUserConnection> {
        UserConnection(
            indication = get(),
            localAudioService = get(),
            rhasspy2HermesConnection = get(),
            rhasspy3WyomingConnection = get(),
            homeAssistantConnection = get(),
            webServerConnection = get(),
            mqttService = get(),
            domainHistory = get(),
        )
    }
    single<IHomeAssistantConnection> { HomeAssistantConnection() }
    single<IRhasspy2HermesConnection> { Rhasspy2HermesConnection() }
    single<IRhasspy3WyomingConnection> { Rhasspy3WyomingConnection() }
    singleOf(::MqttConnectionParamsCreator)
    single<IMqttConnection> {
        MqttConnection(
            appSettingsUtil = get(),
            paramsCreator = get(),
        )
    }
    single<IWebServerConnection> {
        WebServerConnection(
            appSettingsUtil = get(),
            fileStorage = get(),
        )
    }

    factory { params -> UdpConnection(params[0], params[1]) }

    single<IDatabaseLogger> {
        DatabaseLogger(
            nativeApplication = get(),
            externalResultRequest = get(),
            driverFactory = get(),
        )
    }

    single<IFileStorage> {
        FilesStorage(
            nativeApplication = get()
        )
    }

}