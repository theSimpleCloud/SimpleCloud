package eu.thesimplecloud.plugin.startup

import eu.thesimplecloud.client.packets.PacketOutCloudClientLogin
import eu.thesimplecloud.clientserverapi.client.INettyClient
import eu.thesimplecloud.clientserverapi.client.NettyClient
import eu.thesimplecloud.clientserverapi.lib.json.JsonData
import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.client.CloudClientType
import eu.thesimplecloud.api.external.ICloudModule
import eu.thesimplecloud.api.external.ResourceFinder
import eu.thesimplecloud.api.network.packets.service.PacketIOUpdateCloudService
import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.api.service.ServiceState
import eu.thesimplecloud.plugin.ICloudServicePlugin
import eu.thesimplecloud.plugin.impl.CloudAPIImpl
import sun.misc.Resource
import java.io.File
import java.net.URLClassLoader
import kotlin.concurrent.thread


class CloudPlugin(val cloudServicePlugin: ICloudServicePlugin) : ICloudModule {

    companion object {
        @JvmStatic
        lateinit var instance: CloudPlugin
    }

    @Volatile
    private var thisService: ICloudService? = null
    private var updateState = true
    lateinit var communicationClient: INettyClient
        private set
    lateinit var thisServiceName: String
        private set
    private lateinit var nettyThread: Thread
    lateinit var pluginsClassLoader: ClassLoader

    init {
        println("<---------- Starting SimpleCloud-Plugin ---------->")
        instance = this
        CloudAPIImpl()
        if (!loadConfig())
            cloudServicePlugin.shutdown()
        println("<---------- Service-Name: $thisServiceName ---------->")

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
        if (this.thisService == null) this.thisService = CloudAPI.instance.getCloudServiceManager().getCloudServiceByName(thisServiceName)
        while (this.thisService == null || !this.thisService!!.isAuthenticated()) {
            Thread.sleep(10)
            if (this.thisService == null)
                this.thisService = CloudAPI.instance.getCloudServiceManager().getCloudServiceByName(thisServiceName)
        }
        return this.thisService!!
    }

    override fun onEnable() {
        pluginsClassLoader = this::class.java.classLoader
        this.communicationClient.addPacketsByPackage("eu.thesimplecloud.plugin.network.packets")
        this.communicationClient.addPacketsByPackage("eu.thesimplecloud.client.packets")
        this.communicationClient.addPacketsByPackage("eu.thesimplecloud.api.network.packets")
        this.communicationClient.addClassLoader(this::class.java.classLoader)

        nettyThread = thread(true, isDaemon = false, contextClassLoader = this::class.java.classLoader) {
            this.communicationClient.start()
            /*
            this.communicationClient.getPacketIdsSyncPromise().thenAccept {
                val castedClient = this.communicationClient as NettyClient
                val idFromPacket = castedClient.packetManager.getIdFromPacket(PacketIOUpdateCloudService::class.java)
                println("found id: $idFromPacket")
                val packetClassById = castedClient.packetManager.getPacketClassById(idFromPacket!!)!!
                println("before check: ${PacketIOUpdateCloudService::class.java.classLoader == this.pluginsClassLoader}")
                println("classloader of found packet equal to plugins class loader: ${packetClassById.classLoader == this.pluginsClassLoader}")
                println("classloader still equal: ${this::class.java.classLoader == this.pluginsClassLoader}")
                //val loadedClass = Class.forName(PacketIOUpdateCloudService::class.java.name, true, this.pluginsClassLoader)
                val reloadedClass = this.pluginsClassLoader.loadClass(PacketIOUpdateCloudService::class.java.name)
                println("reloaded class classloader: ${this.pluginsClassLoader == reloadedClass.classLoader}")
                println("is system classloader: ${ResourceFinder.getSystemClassLoader() == PacketIOUpdateCloudService::class.java.classLoader}")
                val classLoader = this::class.java.classLoader as URLClassLoader

                val method = classLoader.javaClass.getDeclaredMethod("defineClass", String::class.java, Resource::class.java)
                method.invoke(classLoader, )
                classLoader.urLs.forEach { url ->
                    ResourceFinder.addToClassLoader(url, ResourceFinder.getSystemClassLoader())
                }
            }
             */
        }
        if (this.updateState && thisService().getState() == ServiceState.STARTING) {
            thisService().setState(ServiceState.VISIBLE)
            updateThisService()
        }
    }

    override fun onDisable() {
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