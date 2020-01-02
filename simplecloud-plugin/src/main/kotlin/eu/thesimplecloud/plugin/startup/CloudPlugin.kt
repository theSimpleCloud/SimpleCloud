package eu.thesimplecloud.plugin.startup

import eu.thesimplecloud.client.packets.PacketOutCloudClientLogin
import eu.thesimplecloud.clientserverapi.client.INettyClient
import eu.thesimplecloud.clientserverapi.client.NettyClient
import eu.thesimplecloud.clientserverapi.lib.debug.DebugMessage
import eu.thesimplecloud.clientserverapi.lib.json.JsonData
import eu.thesimplecloud.clientserverapi.lib.packet.packetsender.sendQuery
import eu.thesimplecloud.clientserverapi.lib.resource.ResourceFinder
import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.client.CloudClientType
import eu.thesimplecloud.lib.network.packets.PacketIOPing
import eu.thesimplecloud.lib.network.packets.player.PacketIOConnectCloudPlayer
import eu.thesimplecloud.lib.network.packets.player.PacketIOSendTitleToCloudPlayer
import eu.thesimplecloud.lib.network.packets.service.PacketIOUpdateCloudService
import eu.thesimplecloud.lib.service.ICloudService
import eu.thesimplecloud.lib.service.ServiceState
import eu.thesimplecloud.plugin.ICloudServicePlugin
import eu.thesimplecloud.plugin.impl.CloudLibImpl
import java.io.File
import java.net.URLClassLoader
import java.util.*
import kotlin.concurrent.thread


class CloudPlugin(val cloudServicePlugin: ICloudServicePlugin, val classLoader: URLClassLoader) {

    companion object {
        lateinit var instance: CloudPlugin
    }

    @Volatile
    private var thisService: ICloudService? = null
    private var updateState = true
    lateinit var communicationClient: INettyClient
        private set
    lateinit var thisServiceName: String
        private set
    private val nettyThread: Thread

    init {
        println("<---------- Starting SimpleCloud-Plugin ---------->")
        instance = this
        CloudLibImpl()
        if (!loadConfig())
            cloudServicePlugin.shutdown()
        println("<---------- Service-Name: $thisServiceName ---------->")

        this.communicationClient.addPacketsByPackage("eu.thesimplecloud.plugin.network.packets")
        this.communicationClient.addPacketsByPackage("eu.thesimplecloud.client.packets")
        this.communicationClient.addPacketsByPackage("eu.thesimplecloud.lib.network.packets")
        this.communicationClient.addClassLoader(ResourceFinder.getSystemClassLoader(), this.classLoader)

        nettyThread = thread {
            this.communicationClient.start()
        }
        Runtime.getRuntime().addShutdownHook(Thread {
            try {
                this.communicationClient.shutdown()
            } catch (e: Exception) {
            }

        })
        this.communicationClient.getPacketIdsSyncPromise().addResultListener {
            println("<-------- Connection is now set up -------->")
            this.communicationClient.sendUnitQuery(PacketOutCloudClientLogin(CloudClientType.SERVICE, thisServiceName))
        }
    }

    /**
     * Returns whether the config was loaded successful
     */
    fun loadConfig(): Boolean {
        val jsonData = JsonData.fromJsonFile(File("SIMPLE-CLOUD.json")) ?: return false
        thisServiceName = jsonData.getString("serviceName") ?: return false
        val host = jsonData.getString("managerHost") ?: return false
        val port = jsonData.getInt("managerPort") ?: return false
        this.communicationClient = NettyClient(host, port, ConnectionHandlerImpl())
        return true
    }

    @Synchronized
    fun thisService(): ICloudService {
        if (this.thisService == null) this.thisService = CloudLib.instance.getCloudServiceManger().getCloudServiceByName(thisServiceName)
        while (this.thisService == null) {
            Thread.sleep(10)
            this.thisService = CloudLib.instance.getCloudServiceManger().getCloudServiceByName(thisServiceName)
        }
        return this.thisService!!
    }

    fun enable() {
        if (this.updateState && thisService().getState() == ServiceState.STARTING) {
            thisService().setState(ServiceState.VISIBLE)
            updateThisService()
        }
    }

    fun updateThisService() {
        this.communicationClient.sendUnitQuery(PacketIOUpdateCloudService(thisService()))
    }

    /**
     * Prevents the service from updating its state by itself.
     */
    @Synchronized
    fun disableUpdatingState() {
        this.updateState = false
    }

    fun getGroupName(): String {
        val array = this.thisServiceName.split("-".toRegex())
        array.dropLast(1)
        return array.joinToString("-")
    }


}