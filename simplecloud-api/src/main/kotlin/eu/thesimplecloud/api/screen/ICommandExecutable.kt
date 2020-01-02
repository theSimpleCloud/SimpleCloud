package eu.thesimplecloud.api.screen

import eu.thesimplecloud.clientserverapi.server.client.connectedclient.IConnectedClientValue
import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.utils.Nameable

interface ICommandExecutable : IConnectedClientValue, Nameable {

    /**
     * Executes a command on this [ICommandExecutable]
     */
    fun executeCommand(command: String) = CloudAPI.instance.getCommandExecuteManager().executeCommand(this, command)

}