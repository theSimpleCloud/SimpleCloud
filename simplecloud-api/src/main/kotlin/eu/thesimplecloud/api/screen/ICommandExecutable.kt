package eu.thesimplecloud.api.screen

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.utils.Nameable
import eu.thesimplecloud.clientserverapi.server.client.connectedclient.IConnectedClientValue

interface ICommandExecutable : Nameable, IConnectedClientValue {

    /**
     * Executes a command on this [ICommandExecutable]
     */
    fun executeCommand(command: String) = CloudAPI.instance.getCommandExecuteManager().executeCommand(this, command)

}