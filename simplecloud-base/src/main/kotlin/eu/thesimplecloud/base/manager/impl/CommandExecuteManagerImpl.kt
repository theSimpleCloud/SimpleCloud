package eu.thesimplecloud.base.manager.impl

import eu.thesimplecloud.api.client.NetworkComponentType
import eu.thesimplecloud.api.network.packets.screen.PacketIOExecuteCommand
import eu.thesimplecloud.api.screen.ICommandExecutable
import eu.thesimplecloud.api.screen.ICommandExecuteManager
import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.base.manager.startup.Manager

class CommandExecuteManagerImpl : ICommandExecuteManager {

    override fun executeCommand(commandExecutable: ICommandExecutable, command: String) {
        val cloudClientType = if (commandExecutable is ICloudService) NetworkComponentType.SERVICE else NetworkComponentType.WRAPPER
        val wrapperClient = if (commandExecutable is ICloudService) {
            Manager.instance.communicationServer.getClientManager().getClientByClientValue(commandExecutable.getWrapper())
        } else {
            Manager.instance.communicationServer.getClientManager().getClientByClientValue(commandExecutable)
        }
        wrapperClient?.sendUnitQuery(PacketIOExecuteCommand(cloudClientType, commandExecutable.getName(), command))
    }
}