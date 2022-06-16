/*
 * MIT License
 *
 * Copyright (C) 2020 The SimpleCloud authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package eu.thesimplecloud.base.manager.commands

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.command.ICommandSender
import eu.thesimplecloud.api.extension.sendPacketToAllAuthenticatedWrapperClients
import eu.thesimplecloud.base.manager.config.JvmArgumentsConfigLoader
import eu.thesimplecloud.base.manager.network.packets.PacketOutReloadExistingModules
import eu.thesimplecloud.base.manager.serviceversion.ManagerServiceVersionHandler
import eu.thesimplecloud.base.manager.startup.Manager
import eu.thesimplecloud.launcher.console.command.CommandType
import eu.thesimplecloud.launcher.console.command.ICommandHandler
import eu.thesimplecloud.launcher.console.command.annotations.Command
import eu.thesimplecloud.launcher.console.command.annotations.CommandArgument
import eu.thesimplecloud.launcher.console.command.annotations.CommandSubPath
import eu.thesimplecloud.loader.dependency.DependencyLoader
import kotlin.concurrent.thread

@Command("reload", CommandType.CONSOLE, "cloud.command.reload", ["rl"])
class ReloadCommand : ICommandHandler {

    @CommandSubPath("", "Reloads the cloud")
    fun handleReload(commandSender: ICommandSender) {
        //disable
        Manager.instance.cloudModuleHandler.unloadAllReloadableModules()
        Manager.instance.appClassLoader.clearCachedClasses()
        DependencyLoader.INSTANCE.reset()

        val loadedWrappers = Manager.instance.wrapperFileHandler.loadAll().toMutableList()
        val unknownWrappers =
            loadedWrappers.filter { CloudAPI.instance.getWrapperManager().getWrapperByHost(it.getHost()) == null }
        if (unknownWrappers.isNotEmpty()) {
            unknownWrappers.forEach {
                commandSender.sendProperty("manager.command.reload.wrapper-changed", it.getName())
            }
        }
        loadedWrappers.toMutableList().removeAll(unknownWrappers)
        loadedWrappers.forEach {
            val cachedWrapper = CloudAPI.instance.getWrapperManager().getWrapperByHost(it.getHost())!!
            val wrapperUpdater = cachedWrapper.getUpdater()
            wrapperUpdater.setMaxSimultaneouslyStartingServices(it.getMaxSimultaneouslyStartingServices())
            wrapperUpdater.setMaxMemory(it.getMaxMemory())
            wrapperUpdater.update()
        }
        loadedWrappers.forEach { commandSender.sendProperty("manager.command.reload.wrapper-success", it.getName()) }

        //service versions
        (CloudAPI.instance.getServiceVersionHandler() as ManagerServiceVersionHandler).reloadServiceVersions()

        //jvm-arguments
        val jvmArgumentsConfigLoader = JvmArgumentsConfigLoader()
        Manager.instance.jvmArgumentsConfig = jvmArgumentsConfigLoader.loadConfig()

        //groups
        val loadedGroups = Manager.instance.cloudServiceGroupFileHandler.loadAll().toMutableList()
        val unknownGroups = loadedGroups.filter {
            CloudAPI.instance.getCloudServiceGroupManager().getServiceGroupByName(it.getName()) == null
        }
        if (unknownGroups.isNotEmpty()) {
            unknownGroups.forEach {
                commandSender.sendProperty("manager.command.reload.group-changed", it.getName())
            }
        }
        loadedGroups.toMutableList().removeAll(unknownGroups)
        loadedGroups.forEach { CloudAPI.instance.getCloudServiceGroupManager().update(it) }
        loadedGroups.forEach { commandSender.sendProperty("manager.command.reload.group-success", it.getName()) }

        //send all wrappers a packet to reload the modules list
        Manager.instance.communicationServer.getClientManager()
            .sendPacketToAllAuthenticatedWrapperClients(PacketOutReloadExistingModules())

        //enable
        Manager.instance.appClassLoader.clearCachedClasses()
        thread(
            start = true,
            isDaemon = false,
            Manager.instance.appClassLoader
        ) { Manager.instance.cloudModuleHandler.loadAllUnloadedModules() }
    }

    @CommandSubPath("module <name>", "Reloads a specific module")
    fun handleReloadModule(commandSender: ICommandSender, @CommandArgument("name") moduleName: String) {
        val module = Manager.instance.cloudModuleHandler.getLoadedModuleByName(moduleName)

        if (module == null) {
            commandSender.sendProperty("manager.command.reload.module.not-exists")
            return
        }

        Manager.instance.cloudModuleHandler.unloadModule(module.cloudModule)
        Manager.instance.cloudModuleHandler.loadSingleModuleFromFile(module.file)
    }

}