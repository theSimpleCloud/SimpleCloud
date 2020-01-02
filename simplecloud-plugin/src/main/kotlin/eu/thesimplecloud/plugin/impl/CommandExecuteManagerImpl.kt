package eu.thesimplecloud.plugin.impl

import eu.thesimplecloud.api.client.CloudClientType
import eu.thesimplecloud.api.network.packets.screen.PacketIOExecuteCommand
import eu.thesimplecloud.api.screen.ICommandExecutable
import eu.thesimplecloud.api.screen.ICommandExecuteManager
import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.plugin.startup.CloudPlugin

class CommandExecuteManagerImpl : ICommandExecuteManager {

    override fun executeCommand(commandExecutable: ICommandExecutable, command: String) {
        val cloudClientType = if (commandExecutable is ICloudService) CloudClientType.SERVICE else CloudClientType.WRAPPER
        CloudPlugin.instance.communicationClient.sendUnitQuery(PacketIOExecuteCommand(cloudClientType, commandExecutable.getName(), command))
    }
}