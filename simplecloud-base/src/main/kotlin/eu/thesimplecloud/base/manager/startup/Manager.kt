package eu.thesimplecloud.base.manager.startup

import eu.thesimplecloud.base.manager.filehandler.CloudServiceGroupFileHandler
import eu.thesimplecloud.base.manager.config.TemplatesConfigLoader
import eu.thesimplecloud.base.manager.filehandler.WrapperFileHandler
import eu.thesimplecloud.base.manager.impl.CloudLibImpl
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

    lateinit var nettyServer: INettyServer<ICommandExecutable>
        private set

    init {
        instance = this
    }

    override fun start() {
        CloudLibImpl()
        createDirectories()
        val launcherConfig = Launcher.instance.launcherConfigLoader.loadConfig()
        Launcher.instance.commandManager.registerAllCommands("eu.thesimplecloud.base.manager.commands")
        this.nettyServer = NettyServer<ICommandExecutable>(launcherConfig.host, launcherConfig.port, ConnectionHandlerImpl())
        GlobalScope.launch {
            nettyServer.start()
        }
        MinecraftJars().checkJars()
        Launcher.instance.setupManager.onAllSetupsCompleted(Consumer {
            wrapperFileHandler.loadAll().forEach { CloudLib.instance.getWrapperManager().updateWrapper(it) }
            cloudServiceGroupFileHandler.loadAll().forEach { CloudLib.instance.getCloudServiceGroupManager().updateGroup(it) }
            templatesFileHandler.loadConfig().templates.forEach { CloudLib.instance.getTemplateManager().addTemplate(DefaultTemplate(it)) }
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

    override fun shutdown() {
    }

    override fun isActive(): Boolean = true


}