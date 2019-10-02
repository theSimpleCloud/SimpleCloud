package eu.thesimplecloud.launcher.console.command

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 07.09.2019
 * Time: 14:33
 */
interface ICommandSender {

    /**
     * Sends a message to this [ICommandSender]
     */
    fun sendMessage(message: String)

}