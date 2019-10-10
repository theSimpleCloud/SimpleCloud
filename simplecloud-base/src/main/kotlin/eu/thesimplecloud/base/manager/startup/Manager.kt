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
import java.io.File

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
        this.nettyServer = NettyServer<ICommandExecutable>(launcherConfig.host, launcherConfig.port, ConnectionHandlerImpl())
        this.nettyServer.start()
        MinecraftJars().checkJars()
        wrapperFileHandler.loadAll().forEach { CloudLib.instance.getWrapperManager().updateWrapper(it) }
        templatesFileHandler.loadConfig().list.forEach { CloudLib.instance.getTemplateManager().addTemplate(it) }
        cloudServiceGroupFileHandler.loadAll().forEach { CloudLib.instance.getCloudServiceGroupManager().updateGroup(it) }
    }

    fun createDirectories() {
        val directoryPathsClass = DirectoryPaths::class.java
        val fields = directoryPathsClass.declaredFields
        fields.forEach { it.isAccessible = true }
        fields.map { it.get(DirectoryPaths.paths) }.map { it as String }.map { File(it).mkdirs() }
    }

    override fun shutdown() {
    }

    override fun isActive(): Boolean = true


}