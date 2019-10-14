package eu.thesimplecloud.base.manager.startup

import eu.thesimplecloud.base.manager.filehandler.CloudServiceGroupFileHandler
import eu.thesimplecloud.base.manager.config.TemplatesConfigLoader
import eu.thesimplecloud.base.manager.filehandler.WrapperFileHandler
import eu.thesimplecloud.base.manager.impl.CloudLibImpl
import eu.thesimplecloud.base.manager.startup.server.ConnectionHandlerImpl
import eu.thesimplecloud.base.manager.startup.server.ServerHandlerImpl
import eu.thesimplecloud.clientserverapi.server.INettyServer
import eu.thesimplecloud.clientserverapi.server.NettyServer
import eu.thesimplecloud.launcher.application.ICloudApplication
import eu.thesimplecloud.launcher.startup.Launcher
import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.directorypaths.DirectoryPaths
import eu.thesimplecloud.lib.screen.ICommandExecutable
import eu.thesimplecloud.lib.template.impl.DefaultTemplate
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.util.function.Consumer

class Manager : ICloudApplication {

    val cloudServiceGroupFileHandler = CloudServiceGroupFileHandler()
    val wrapperFileHandler = WrapperFileHandler()
    val templatesFileHandler = TemplatesConfigLoader()

    companion object {
        lateinit var instance: Manager
    }

    lateinit var communicationServer: INettyServer<ICommandExecutable>
        private set
    lateinit var templateServer: INettyServer<ICommandExecutable>
        private set

    init {
        instance = this
    }

    override fun onEnable() {
        CloudLibImpl()
        createDirectories()
        val launcherConfig = Launcher.instance.launcherConfigLoader.loadConfig()
        Launcher.instance.commandManager.registerAllCommands(this, "eu.thesimplecloud.base.manager.commands")
        this.communicationServer = NettyServer<ICommandExecutable>(launcherConfig.host, launcherConfig.port, ConnectionHandlerImpl(), ServerHandlerImpl())
        this.templateServer = NettyServer<ICommandExecutable>(launcherConfig.host, launcherConfig.port + 1, ConnectionHandlerImpl(), ServerHandlerImpl())
        GlobalScope.launch { communicationServer.start() }
        GlobalScope.launch { templateServer.start() }
        MinecraftJars().checkJars()
        Launcher.instance.setupManager.onAllSetupsCompleted(Consumer {
            wrapperFileHandler.loadAll().forEach { CloudLib.instance.getWrapperManager().updateWrapper(it) }
            cloudServiceGroupFileHandler.loadAll().forEach { CloudLib.instance.getCloudServiceGroupManager().updateGroup(it) }
            templatesFileHandler.loadConfig().templates.forEach { CloudLib.instance.getTemplateManager().addTemplate(DefaultTemplate(it)) }

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
                File(DirectoryPaths.paths.templatesPath + "SERVER_EVERY"),
                File(DirectoryPaths.paths.templatesPath + "PROXY_EVERY")
        )) {
            file.mkdirs()
        }
    }

    override fun onDisable() {
    }


}