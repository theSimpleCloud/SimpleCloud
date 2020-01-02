package eu.thesimplecloud.lib.exception

/**
 * This exception is thrown when a service is not available or not connected to the manager.
 */
class UnreachableServiceException(reason: String) : Exception(reason) {
}