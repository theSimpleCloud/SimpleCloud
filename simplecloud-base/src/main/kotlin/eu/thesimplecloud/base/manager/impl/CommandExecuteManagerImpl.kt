package eu.thesimplecloud.base.manager.impl

import eu.thesimplecloud.base.manager.startup.Manager
import eu.thesimplecloud.api.client.CloudClientType
import eu.thesimplecloud.api.network.packets.screen.PacketIOExecuteCommand
import eu.thesimplecloud.api.screen.ICommandExecutable
import eu.thesimplecloud.api.screen.ICommandExecuteManager
import eu.thesimplecloud.api.service.ICloudService

class CommandExecuteManagerImpl : ICommandExecuteManager {

    override fun executeCommand(commandExecutable: ICommandExecutable, command: String) {
        val cloudClientType = if (commandExecutable is ICloudService) CloudClientType.SERVICE else CloudClientType.WRAPPER
        val wrapperClient = if (commandExecutable is ICloudService) {
            Manager.instance.communicationServer.getClientManager().getClientByClientValue(commandExecutable.getWrapper())
        } else {
            Manager.instance.communicationServer.getClientManager().getClientByClientValue(commandExecutable)
        }
        wrapperClient?.sendUnitQuery(PacketIOExecuteCommand(cloudClientType, commandExecutable.getName(), command))
    }
}