package eu.thesimplecloud.lib.screen

import eu.thesimplecloud.clientserverapi.server.client.connectedclient.IConnectedClientValue
import eu.thesimplecloud.lib.CloudLib

interface ICommandExecutable : IConnectedClientValue {

    /**
     * Executes a command on this [ICommandExecutable]
     */
    fun executeCommand(command: String) = CloudLib.instance.getCommandExecuteManager().executeCommand(this, command)

}