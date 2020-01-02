package eu.thesimplecloud.lib.service

enum class ServiceState {

    /**
     * The service was registered but not started yet.
     */
    PREPARED,
    /**
     * The service is registered and is currently starting
     */
    STARTING,
    /**
     * The service was started and will now be shown on the cloud signs.
     */
    VISIBLE,
    /**
     * The service is online and will not be shown on the cloud signs.
     */
    INVISIBLE,
    /**
     * The service was stopped
     */
    CLOSED

}