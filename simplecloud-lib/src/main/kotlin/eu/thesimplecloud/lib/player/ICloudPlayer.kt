package eu.thesimplecloud.lib.player

import eu.thesimplecloud.clientserverapi.lib.packet.communicationpromise.ICommunicationPromise
import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.player.text.CloudText
import eu.thesimplecloud.lib.service.ICloudService

interface ICloudPlayer : IOfflineCloudPlayer {

    /**
     * Get the numerical client version of the player attempting to log in.
     *
     * @return the protocol version of the remote client
     */
    fun getVersion(): Int

    /**
     * Sends a message to this player.
     */
    fun sendMessage(cloudText: CloudText) = CloudLib.instance.getCloudPlayerManager().sendMessageToPlayer(this, cloudText)

    /**
     * Sends a message to this player.
     */
    fun sendMessage(message: String) = sendMessage(CloudText(message))

    /**
     * Sends this player to the specified [cloudService]
     * @throws IllegalArgumentException when the specified service is a proxy service.
     * @return a promise that will be completed when then player is connected to the specified service.
     */
    fun sendToService(cloudService: ICloudService) = CloudLib.instance.getCloudPlayerManager().sendPlayerToService(this, cloudService)

    /**
     * Kicks this player form the network.
     */
    fun kick(message: String) = CloudLib.instance.getCloudPlayerManager().kickPlayer(this, message)

    /**
     * Kicks this player from the network.
     */
    fun kick() = kick("§cYou were kicked from the network.")

    /**
     * Sends a title to this player.
     */
    fun sendTitle(title: String, subTitle: String, fadeIn: Int, stay: Int, fadeOut: Int) = CloudLib.instance.getCloudPlayerManager().sendTitle(this, title, subTitle, fadeIn, stay, fadeOut)

    /**
     * Sends a action bar to this player
     */
    fun sendActionBar(actionbar: String) = CloudLib.instance.getCloudPlayerManager().sendActionbar(this, actionbar)

    /**
     * Returns the name of the proxy the player is connected to.
     */
    fun getConnectedProxyName(): String

    /**
     * Returns the name of the server the player is connected to.
     */
    fun getConnectedServerName(): String?

    /**
     * Returns the proxy this player is connected to.
     */
    fun getConnectedProxy(): ICloudService? = CloudLib.instance.getCloudServiceManger().getCloudService(getConnectedProxyName())

    /**
     * Returns the server this player is connected to.
     */
    fun getConnectedServer(): ICloudService? = getConnectedServerName()?.let { CloudLib.instance.getCloudServiceManger().getCloudService(it) }

    /**
     * Tells the manager that this client wants to receive updates of this player.
     */
    fun enableUpdates() = CloudLib.instance.getCloudPlayerManager().setUpdates(this, true, CloudLib.instance.getThisSidesName())

    /**
     * Tells the manager that this client no longer wants to receive updates of this player.
     */
    fun disableUpdates() = CloudLib.instance.getCloudPlayerManager().setUpdates(this, false, CloudLib.instance.getThisSidesName())

    /**
     * Lets this player executes the specified [command]
     */
    fun forceCommandExecution(command: String) = CloudLib.instance.getCloudPlayerManager().forcePlayerCommandExecution(this, command)

}