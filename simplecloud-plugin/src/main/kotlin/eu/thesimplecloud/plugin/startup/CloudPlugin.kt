package eu.thesimplecloud.plugin.startup

import eu.thesimplecloud.client.packets.PacketOutCloudClientLogin
import eu.thesimplecloud.clientserverapi.client.INettyClient
import eu.thesimplecloud.clientserverapi.client.NettyClient
import eu.thesimplecloud.clientserverapi.lib.json.JsonData
import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.client.CloudClientType
import eu.thesimplecloud.lib.network.packets.service.PacketIOUpdateCloudService
import eu.thesimplecloud.lib.service.ICloudService
import eu.thesimplecloud.lib.service.ServiceState
import eu.thesimplecloud.plugin.ICloudServicePlugin
import eu.thesimplecloud.plugin.impl.CloudLibImpl
import java.io.File
import javax.xml.bind.JAXBElement
import kotlin.concurrent.thread


class CloudPlugin(val cloudServicePlugin: ICloudServicePlugin) {

    companion object {
        lateinit var instance: CloudPlugin
    }

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
        this.communicationClient.addPacketsByPackage("eu.thesimplecloud.client.packets")
        this.communicationClient.addPacketsByPackage("eu.thesimplecloud.lib.network.packets")
        nettyThread = thread {
            this.communicationClient.start()
        }
        Runtime.getRuntime().addShutdownHook(Thread {
            try {
                this.communicationClient.shutdown()
            } catch (e: Exception) { }

        })
        this.communicationClient.getPacketIdsSyncPromise().addResultListener {
            println("<-------- Connection is now set up -------->")
            this.communicationClient.sendQuery(PacketOutCloudClientLogin(CloudClientType.SERVICE, thisServiceName))
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

    fun thisService(): ICloudService {
        thisService = CloudLib.instance.getCloudServiceManger().getCloudService(thisServiceName)
        while (thisService == null) {
            Thread.sleep(20)
            thisService = CloudLib.instance.getCloudServiceManger().getCloudService(thisServiceName)
        }
        return thisService!!
    }

    fun enable() {
        if (this.updateState && thisService().getState() == ServiceState.STARTING) {
            thisService().setState(ServiceState.LOBBY)
            updateThisService()
        }
    }

    fun updateThisService() {
        thisService?.let { this.communicationClient.sendQuery(PacketIOUpdateCloudService(it)) }
    }

    fun disableUpdateState() {
        this.updateState = false
    }

    fun getGroupName(): String {
        val array = this.thisServiceName.split("-".toRegex())
        array.dropLast(1)
        return array.joinToString("-")
    }


}