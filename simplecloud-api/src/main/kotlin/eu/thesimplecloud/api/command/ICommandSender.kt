package eu.thesimplecloud.api.command

import eu.thesimplecloud.api.exception.NoSuchPlayerException
import eu.thesimplecloud.api.exception.UnreachableServiceException
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

interface ICommandSender {

    /**
     * Sends a message to this [ICommandSender]
     * @return a promise that completes when the message was sent.
     */
    fun sendMessage(message: String): ICommunicationPromise<Unit>

    /**
     * Checks whether this sender has the specified [permission]
     * @return a promise that is completed when the permission is checked, or
     * when an exception is encountered. [ICommunicationPromise.isSuccess] indicates success
     * or failure. If the sender is a ConsoleSender this method returns true.
     * The promise will fail with:
     * - [UnreachableServiceException] if the proxy server the sender is connected is not reachable.
     * - [NoSuchPlayerException] if the sender cannot be found on the proxy.
     */
    fun hasPermission(permission: String): ICommunicationPromise<Boolean>

    /**
     * Checks if the sender has the specified [permission]
     * This methods block until the result is ready
     * @return whether the player has the permission or false if there was an error
     */
    fun hasPermissionSync(permission: String): Boolean = hasPermission(permission).getBlockingOrNull() ?: false

}