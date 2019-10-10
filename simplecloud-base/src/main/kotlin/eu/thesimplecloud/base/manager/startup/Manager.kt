package eu.thesimplecloud.base.manager.startup

import eu.thesimplecloud.base.manager.config.ManagerConfigLoader
import eu.thesimplecloud.base.manager.filehandler.CloudServiceGroupFileHandler
import eu.thesimplecloud.base.manager.filehandler.TemplatesFileHandler
import eu.thesimplecloud.base.manager.filehandler.WrapperFileHandler
import eu.thesimplecloud.base.manager.impl.CloudLibImpl
import eu.thesimplecloud.clientserverapi.server.INettyServer
import eu.thesimplecloud.clientserverapi.server.NettyServer
import eu.thesimplecloud.launcher.application.ICloudApplication
import eu.thesimplecloud.launcher.startup.Launcher
import eu.thesimplecloud.lib.screen.ICommandExecutable

class Manager : ICloudApplication {

    val cloudServiceGroupFileHandler = CloudServiceGroupFileHandler()
    val wrapperFileHandler = WrapperFileHandler()
    val templatesFileHandler = TemplatesFileHandler()

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
        val launcherConfig = Launcher.instance.launcherConfigLoader.loadConfig()
        this.nettyServer = NettyServer<ICommandExecutable>(launcherConfig.host, launcherConfig.port, ConnectionHandlerImpl())
        this.nettyServer.start()
        MinecraftJars().checkJars()
        
    }

    override fun shutdown() {
    }

    override fun isActive(): Boolean = true


}