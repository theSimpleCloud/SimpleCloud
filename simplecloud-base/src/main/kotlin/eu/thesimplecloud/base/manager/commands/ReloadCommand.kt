package eu.thesimplecloud.base.manager.commands

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.command.ICommandSender
import eu.thesimplecloud.api.extension.sendPacketToAllAuthenticatedWrapperClients
import eu.thesimplecloud.api.wrapper.IWritableWrapperInfo
import eu.thesimplecloud.base.manager.network.packets.PacketOutReloadExistingModules
import eu.thesimplecloud.base.manager.startup.Manager
import eu.thesimplecloud.launcher.console.command.CommandType
import eu.thesimplecloud.launcher.console.command.ICommandHandler
import eu.thesimplecloud.launcher.console.command.annotations.Command
import eu.thesimplecloud.launcher.console.command.annotations.CommandSubPath
import eu.thesimplecloud.launcher.extension.sendMessage
import kotlin.concurrent.thread

@Command("reload", CommandType.CONSOLE, "simplecloud.command.reload", ["rl"])
class ReloadCommand : ICommandHandler {

    @CommandSubPath("", "Reloads the cloud")
    fun handleReload(commandSender: ICommandSender) {
        //disable
        Manager.instance.cloudModuleHandler.unloadAllReloadableModules()
        Manager.instance.appClassLoader.clearCachedClasses()

        val loadedWrappers = Manager.instance.wrapperFileHandler.loadAll().toMutableList()
        val unknownWrappers = loadedWrappers.filter { CloudAPI.instance.getWrapperManager().getWrapperByHost(it.getHost()) == null }
        if (unknownWrappers.isNotEmpty()) {
            unknownWrappers.forEach {
                commandSender.sendMessage("manager.command.reload.wrapper-changed", "Failed to reload wrapper %WRAPPER%", it.getName(), " because the host has changed.")
            }
        }
        loadedWrappers.toMutableList().removeAll(unknownWrappers)
        loadedWrappers.forEach {
            val cachedWrapper = CloudAPI.instance.getWrapperManager().getWrapperByHost(it.getHost()) as IWritableWrapperInfo
            cachedWrapper.setMaxSimultaneouslyStartingServices(it.getMaxSimultaneouslyStartingServices())
            cachedWrapper.setMaxMemory(it.getMaxMemory())
            CloudAPI.instance.getWrapperManager().update(cachedWrapper)
        }
        loadedWrappers.forEach { commandSender.sendMessage("manager.command.reload.wrapper-success", "Reloaded wrapper %WRAPPER%", it.getName(), ".") }

        //groups
        val loadedGroups = Manager.instance.cloudServiceGroupFileHandler.loadAll().toMutableList()
        val unknownGroups = loadedGroups.filter { CloudAPI.instance.getCloudServiceGroupManager().getServiceGroupByName(it.getName()) == null }
        if (unknownGroups.isNotEmpty()) {
            unknownGroups.forEach {
                commandSender.sendMessage("manager.command.reload.group-changed", "Failed to reload group %GROUP%", it.getName(), " because the name has changed.")
            }
        }
        loadedGroups.toMutableList().removeAll(unknownGroups)
        loadedGroups.forEach { CloudAPI.instance.getCloudServiceGroupManager().update(it) }
        loadedGroups.forEach { commandSender.sendMessage("manager.command.reload.group-success", "Reloaded group %GROUP%", it.getName(), ".") }

        //send all wrappers a packet to reload the modules list
        Manager.instance.communicationServer.getClientManager().sendPacketToAllAuthenticatedWrapperClients(PacketOutReloadExistingModules())

        //enable
        thread(start = true, isDaemon = false) { Manager.instance.cloudModuleHandler.loadAllUnloadedModules() }
    }

}