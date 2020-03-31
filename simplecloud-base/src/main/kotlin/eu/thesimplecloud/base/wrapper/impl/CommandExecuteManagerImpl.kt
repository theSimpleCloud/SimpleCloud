package eu.thesimplecloud.base.wrapper.impl

import eu.thesimplecloud.api.client.CloudClientType
import eu.thesimplecloud.api.network.packets.screen.PacketIOExecuteCommand
import eu.thesimplecloud.api.screen.ICommandExecutable
import eu.thesimplecloud.api.screen.ICommandExecuteManager
import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.api.wrapper.IWrapperInfo
import eu.thesimplecloud.base.wrapper.startup.Wrapper
import eu.thesimplecloud.launcher.startup.Launcher

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
        Wrapper.instance.communicationClient.sendUnitQuery(PacketIOExecuteCommand(cloudClientType, commandExecutable.getName(), command))
    }
}