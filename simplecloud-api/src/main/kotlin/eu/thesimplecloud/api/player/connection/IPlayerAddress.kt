package eu.thesimplecloud.api.player.connection

interface IPlayerAddress {

    /**
     * Returns the ip address of the player
     */
    fun getHostname(): String

    /**
     * Returns the port of the player
     */
    fun getPort(): Int

}