package eu.thesimplecloud.api.command

import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

interface ICommandSender {

    /**
     * Sends a message to this [ICommandSender]
     * @return a promise that completes when the message was sent.
     */
    fun sendMessage(message: String): ICommunicationPromise<Unit>

}