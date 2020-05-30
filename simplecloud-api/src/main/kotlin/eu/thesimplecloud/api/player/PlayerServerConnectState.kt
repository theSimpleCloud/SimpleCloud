package eu.thesimplecloud.api.player

enum class PlayerServerConnectState {

    /**
     * The player is currently connecting to a server
     */
    CONNECTING,

    /**
     * The player is fully connected to the server.
     */
    CONNECTED

}
