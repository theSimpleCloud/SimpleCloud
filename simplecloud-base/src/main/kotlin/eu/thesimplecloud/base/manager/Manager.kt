package eu.thesimplecloud.base.manager

import eu.thesimplecloud.base.manager.config.ManagerConfigLoader
import eu.thesimplecloud.clientserverapi.server.INettyServer
import eu.thesimplecloud.clientserverapi.server.NettyServer
import eu.thesimplecloud.launcher.application.ICloudApplication
import eu.thesimplecloud.lib.screen.ICommandExecutable

class Manager : ICloudApplication {

    private lateinit var nettyServer: INettyServer<ICommandExecutable>

    override fun start() {
        val managerConfigLoader = ManagerConfigLoader()
        if (!managerConfigLoader.doesFileExist()) {

        }
    }

    override fun shutdown() {
    }

    override fun isActive(): Boolean = true



}