package eu.thesimplecloud.lib.exception

/**
 * This exception is used to indicate that the proxy was unable to send the player to a service.
 */
class PlayerConnectException(message: String) : Exception(message) {
}