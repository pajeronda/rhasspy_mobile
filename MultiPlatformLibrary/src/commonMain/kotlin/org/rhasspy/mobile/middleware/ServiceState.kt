package org.rhasspy.mobile.middleware

import dev.icerock.moko.resources.StringResource
import org.rhasspy.mobile.MR
//TODO better human readable information
sealed class ServiceState(val information: String? = null) {

    object Pending : ServiceState()

    object Loading : ServiceState()

    class Success(information: String? = null) : ServiceState(information)

    class Warning(information: String? = null) : ServiceState(information)

    sealed class Error(val exception: Throwable) : ServiceState() {
        sealed class HttpClientServiceErrorType(exception: Throwable, val humanReadable: StringResource) : Error(exception) {
            class IllegalArgumentException(exception: Throwable) : HttpClientServiceErrorType(exception, MR.strings.defaultText)
            class InvalidTLSRecordType(exception: Throwable) : HttpClientServiceErrorType(exception, MR.strings.defaultText) // Invalid TLS record type code: 72)
            class UnresolvedAddressException(exception: Throwable) : HttpClientServiceErrorType(exception, MR.strings.defaultText) //server cannot be reached
            class ConnectException(exception: Throwable) : HttpClientServiceErrorType(exception, MR.strings.defaultText)
            class ConnectionRefused(exception: Throwable) : HttpClientServiceErrorType(exception, MR.strings.defaultText) //wrong port or address
            class Unknown(exception: Throwable) : HttpClientServiceErrorType(exception, MR.strings.defaultText)
        }

        class Unknown(exception: Throwable) : Error(exception)
    }

    object Disabled : ServiceState()

}