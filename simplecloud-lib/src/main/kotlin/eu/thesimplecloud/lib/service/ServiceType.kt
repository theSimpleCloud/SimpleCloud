package eu.thesimplecloud.lib.service

enum class ServiceType {

    /**
     * Represents a proxy service
     */
    PROXY,
    /**
     * Represents a normal minecraft server
     */
    SERVER,
    /**
     * Represents a lobby server
     */
    LOBBY;

    /**
     * Returns whether this service type is proxy service
     */
    fun isProxy(): Boolean {
        return this == PROXY
    }

    /**
     * Returns whether this service type is a lobby service
     */
    fun isLobby(): Boolean {
        return this == LOBBY
    }

}