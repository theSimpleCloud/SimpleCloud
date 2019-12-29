package eu.thesimplecloud.plugin.impl

import eu.thesimplecloud.lib.client.CloudClientType
import eu.thesimplecloud.lib.network.packets.screen.PacketIOExecuteCommand
import eu.thesimplecloud.lib.screen.ICommandExecutable
import eu.thesimplecloud.lib.screen.ICommandExecuteManager
import eu.thesimplecloud.lib.service.ICloudService
import eu.thesimplecloud.plugin.startup.CloudPlugin

class CommandExecuteManagerImpl : ICommandExecuteManager {

    override fun executeCommand(commandExecutable: ICommandExecutable, command: String) {
        val cloudClientType = if (commandExecutable is ICloudService) CloudClientType.SERVICE else CloudClientType.WRAPPER
        CloudPlugin.instance.communicationClient.sendUnitQuery(PacketIOExecuteCommand(cloudClientType, commandExecutable.getName(), command))
    }
}