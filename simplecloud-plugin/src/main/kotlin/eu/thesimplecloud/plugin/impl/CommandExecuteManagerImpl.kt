package eu.thesimplecloud.plugin.impl

import eu.thesimplecloud.api.client.NetworkComponentType
import eu.thesimplecloud.api.network.packets.screen.PacketIOExecuteCommand
import eu.thesimplecloud.api.screen.ICommandExecutable
import eu.thesimplecloud.api.screen.ICommandExecuteManager
import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.plugin.startup.CloudPlugin

class CommandExecuteManagerImpl : ICommandExecuteManager {

    override fun executeCommand(commandExecutable: ICommandExecutable, command: String) {
        val cloudClientType = if (commandExecutable is ICloudService) NetworkComponentType.SERVICE else NetworkComponentType.WRAPPER
        CloudPlugin.instance.communicationClient.sendUnitQuery(PacketIOExecuteCommand(cloudClientType, commandExecutable.getName(), command))
    }
}