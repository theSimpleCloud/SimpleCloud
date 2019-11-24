package eu.thesimplecloud.base.manager.startup

import eu.thesimplecloud.base.manager.filehandler.CloudServiceGroupFileHandler
import eu.thesimplecloud.base.manager.config.TemplatesConfigLoader
import eu.thesimplecloud.base.manager.filehandler.WrapperFileHandler
import eu.thesimplecloud.base.manager.impl.CloudLibImpl
import eu.thesimplecloud.base.manager.listener.CloudListener
import eu.thesimplecloud.base.manager.service.ServiceHandler
import eu.thesimplecloud.base.manager.startup.server.CommunicationConnectionHandlerImpl
import eu.thesimplecloud.base.manager.startup.server.ServerHandlerImpl
import eu.thesimplecloud.base.manager.startup.server.TemplateConnectionHandlerImpl
import eu.thesimplecloud.clientserverapi.lib.filetransfer.directory.DirectorySyncManager
import eu.thesimplecloud.clientserverapi.server.INettyServer
import eu.thesimplecloud.clientserverapi.server.NettyServer
import eu.thesimplecloud.launcher.application.ICloudApplication
import eu.thesimplecloud.launcher.startup.Launcher
import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.directorypaths.DirectoryPaths
import eu.thesimplecloud.lib.screen.ICommandExecutable
import java.io.File
import java.util.function.Consumer
import kotlin.concurrent.thread

class Manager : ICloudApplication {

    val cloudServiceGroupFileHandler = CloudServiceGroupFileHandler()
    val wrapperFileHandler = WrapperFileHandler()
    val templatesConfigLoader = TemplatesConfigLoader()
    val serviceHandler: ServiceHandler = ServiceHandler()

    companion object {
        lateinit var instance: Manager
    }

    val communicationServer: INettyServer<ICommandExecutable>
    val templateServer: INettyServer<ICommandExecutable>

    init {
        instance = this
        CloudLibImpl()
        CloudLib.instance.getEventManager().registerListener(this, CloudListener())
        val launcherConfig = Launcher.instance.launcherConfigLoader.loadConfig()
        this.communicationServer = NettyServer<ICommandExecutable>(launcherConfig.host, launcherConfig.port, CommunicationConnectionHandlerImpl(), ServerHandlerImpl())
        this.templateServer = NettyServer<ICommandExecutable>(launcherConfig.host, launcherConfig.port + 1, TemplateConnectionHandlerImpl(), ServerHandlerImpl())
        this.communicationServer.addPacketsByPackage("eu.thesimplecloud.lib.network.packets")
        this.communicationServer.addPacketsByPackage("eu.thesimplecloud.base.manager.network.packets")
        this.templateServer.addPacketsByPackage("eu.thesimplecloud.base.manager.network.packets.template")
        this.serviceHandler.startThread()
        thread(start = true, isDaemon = false) { communicationServer.start() }
        thread(start = true, isDaemon = false) { templateServer.start() }
        createDirectories()
        this.templateServer.getDirectorySyncManager().createDirectorySync(File(DirectoryPaths.paths.templatesPath), DirectoryPaths.paths.templatesPath)
        this.templateServer.getDirectorySyncManager().createDirectorySync(File(DirectoryPaths.paths.modulesPath), DirectoryPaths.paths.modulesPath)
    }

    override fun onEnable() {
        Launcher.instance.commandManager.registerAllCommands(this, "eu.thesimplecloud.base.manager.commands")
        Launcher.instance.setupManager.onAllSetupsCompleted(Consumer {
            wrapperFileHandler.loadAll().forEach { CloudLib.instance.getWrapperManager().updateWrapper(it) }
            cloudServiceGroupFileHandler.loadAll().forEach { CloudLib.instance.getCloudServiceGroupManager().updateGroup(it) }
            templatesConfigLoader.loadConfig().templates.forEach { CloudLib.instance.getTemplateManager().updateTemplate(it) }

            if (CloudLib.instance.getWrapperManager().getAllWrappers().isNotEmpty()) {
                Launcher.instance.consoleSender.sendMessage("manager.startup.loaded.wrappers", "Loaded following wrappers:")
                CloudLib.instance.getWrapperManager().getAllWrappers().forEach { Launcher.instance.consoleSender.sendMessage("- ${it.getName()}") }
            }

            if (CloudLib.instance.getTemplateManager().getAllTemplates().isNotEmpty()){
                Launcher.instance.consoleSender.sendMessage("manager.startup.loaded.templates", "Loaded following templates:")
                CloudLib.instance.getTemplateManager().getAllTemplates().forEach { Launcher.instance.consoleSender.sendMessage("- ${it.getName()}") }
            }

            if (CloudLib.instance.getCloudServiceGroupManager().getAllGroups().isNotEmpty()){
                Launcher.instance.consoleSender.sendMessage("manager.startup.loaded.groups", "Loaded following groups:")
                CloudLib.instance.getCloudServiceGroupManager().getAllGroups().forEach { Launcher.instance.consoleSender.sendMessage("- ${it.getName()}") }
            }

        })
    }


    private fun createDirectories() {
        for (file in listOf(
                File(DirectoryPaths.paths.storagePath),
                File(DirectoryPaths.paths.wrappersPath),
                File(DirectoryPaths.paths.minecraftJarsPath),
                File(DirectoryPaths.paths.serverGroupsPath),
                File(DirectoryPaths.paths.lobbyGroupsPath),
                File(DirectoryPaths.paths.proxyGroupsPath),
                File(DirectoryPaths.paths.languagesPath),
                File(DirectoryPaths.paths.modulesPath),
                File(DirectoryPaths.paths.templatesPath),
                File(DirectoryPaths.paths.templatesPath + "EVERY"),
                File(DirectoryPaths.paths.templatesPath + "EVERY_SERVER"),
                File(DirectoryPaths.paths.templatesPath + "EVERY_PROXY")
        )) {
            file.mkdirs()
        }
    }

    override fun onDisable() {
    }


}