package eu.thesimplecloud.lib.service.exception

/**
 * This exception is thrown when a service is not available or not connected to the manager.
 */
class UnavailableServiceException(reason: String) : Exception(reason) {
}