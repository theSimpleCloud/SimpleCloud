package eu.thesimplecloud.base.wrapper.impl

import eu.thesimplecloud.base.wrapper.startup.Wrapper
import eu.thesimplecloud.launcher.startup.Launcher
import eu.thesimplecloud.lib.client.CloudClientType
import eu.thesimplecloud.lib.network.packets.screen.PacketIOExecuteCommand
import eu.thesimplecloud.lib.screen.ICommandExecutable
import eu.thesimplecloud.lib.screen.ICommandExecuteManager
import eu.thesimplecloud.lib.service.ICloudService
import eu.thesimplecloud.lib.wrapper.IWrapperInfo

class CommandExecuteManagerImpl : ICommandExecuteManager {

    override fun executeCommand(commandExecutable: ICommandExecutable, command: String) {
        if (commandExecutable is ICloudService && commandExecutable.getWrapper() == Wrapper.instance.getThisWrapper()) {
            val serviceProcess = Wrapper.instance.cloudServiceProcessManager.getCloudServiceProcessByServiceName(commandExecutable.getName())
            serviceProcess?.executeCommand(command)
            return
        }
        if (commandExecutable is IWrapperInfo && commandExecutable == Wrapper.instance.getThisWrapper()) {
            Launcher.instance.executeCommand(command)
            return
        }
        val cloudClientType = if (commandExecutable is ICloudService) CloudClientType.SERVICE else CloudClientType.WRAPPER
        Wrapper.instance.communicationClient.sendQuery(PacketIOExecuteCommand(cloudClientType, commandExecutable.getName(), command))
    }
}