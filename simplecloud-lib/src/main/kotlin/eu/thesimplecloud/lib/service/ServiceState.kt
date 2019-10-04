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
    LOBBY,
    /**
     * The service is online and will not be shown on the cloud signs.
     */
    INGAME,
    /**
     * The service was stopped
     */
    CLOSED

}