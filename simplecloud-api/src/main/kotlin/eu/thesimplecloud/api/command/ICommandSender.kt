package eu.thesimplecloud.api.command

interface ICommandSender {

    /**
     * Sends a message to this [ICommandSender]
     */
    fun sendMessage(message: String)

}