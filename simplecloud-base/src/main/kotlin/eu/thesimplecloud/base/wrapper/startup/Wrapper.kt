package eu.thesimplecloud.base.wrapper.startup

import eu.thesimplecloud.base.manager.startup.Manager
import eu.thesimplecloud.clientserverapi.client.INettyClient
import eu.thesimplecloud.clientserverapi.client.NettyClient
import eu.thesimplecloud.launcher.application.ICloudApplication
import eu.thesimplecloud.launcher.startup.Launcher
import eu.thesimplecloud.lib.client.CloudClientType
import eu.thesimplecloud.client.packets.PacketOutCloudClientLogin
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File

class Wrapper : ICloudApplication {

    companion object {
        lateinit var instance: Wrapper
    }

    val communicationClient: INettyClient
    val templateClient: INettyClient?

    init {
        instance = this
        val launcherConfig = Launcher.instance.launcherConfigLoader.loadConfig()
        this.communicationClient = NettyClient(launcherConfig.host, launcherConfig.port, ConnectionHandlerImpl())
        this.communicationClient.addPacketsByPackage("eu.thesimplecloud.client.packets")
        this.communicationClient.addPacketsByPackage("eu.thesimplecloud.lib.packets")
        GlobalScope.launch { communicationClient.start() }
        if (isStartedInManagerDirectory()) {
            Launcher.instance.consoleSender.sendMessage("wrapper.startup.template-client.not-activated", "Detected that a manager is running in this directory. Using templates in this folder.")
            Launcher.instance.consoleSender.sendMessage("wrapper.startup.template-client.help-message", "If your'e manager is not running in this directory delete the folder \"storage/wrappers\" and restart the wrapper.")
            this.templateClient = null
        } else {
            this.templateClient = NettyClient(launcherConfig.host, launcherConfig.port + 1)
            GlobalScope.launch { templateClient.start() }
            Launcher.instance.consoleSender.sendMessage("wrapper.startup.template-client.using", "Using an extra client to receive / send templates.")
        }
        this.communicationClient.sendQuery(PacketOutCloudClientLogin(CloudClientType.WRAPPER))
    }

    override fun onEnable() {
    }

    override fun onDisable() {
    }

    fun isStartedInManagerDirectory(): Boolean = File("storage/wrappers/").exists()
}