package eu.thesimplecloud.plugin.proxy.bungee

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.ingamecommand.SynchronizedIngameCommandNamesContainer
import eu.thesimplecloud.api.player.ICloudPlayerManager
import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.api.servicegroup.grouptype.ICloudServerGroup
import eu.thesimplecloud.plugin.impl.player.CloudPlayerManagerBungee
import eu.thesimplecloud.plugin.listener.CloudListener
import eu.thesimplecloud.plugin.proxy.ICloudProxyPlugin
import eu.thesimplecloud.plugin.proxy.bungee.listener.BungeeListener
import eu.thesimplecloud.plugin.proxy.bungee.listener.IngameCommandListener
import eu.thesimplecloud.plugin.startup.CloudPlugin
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.plugin.Plugin
import java.net.InetSocketAddress
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

class CloudBungeePlugin : Plugin(), ICloudProxyPlugin {

    val lobbyConnector = LobbyConnector()

    @Volatile
    var synchronizedIngameCommandNamesContainer = SynchronizedIngameCommandNamesContainer()
        private set

    companion object {
        @JvmStatic
        lateinit var instance: CloudBungeePlugin
    }

    init {
        instance = this
    }

    override fun shutdown() {
        ProxyServer.getInstance().stop()
    }

    override fun addServiceToProxy(cloudService: ICloudService) {
        if (ProxyServer.getInstance().getServerInfo(cloudService.getName()) != null) {
            throw IllegalArgumentException("Service is already registered!")
        }

        if (cloudService.getServiceType().isProxy() || !cloudService.isOnline())
            return
        val cloudServiceGroup = cloudService.getServiceGroup()
        if ((cloudServiceGroup as ICloudServerGroup).getHiddenAtProxyGroups().contains(CloudPlugin.instance.getGroupName()))
            return
        println("Registered service ${cloudService.getName()}")
        val socketAddress = InetSocketAddress(cloudService.getHost(), cloudService.getPort())
        registerService(cloudService.getName(), cloudService.getUniqueId(), socketAddress)
    }

    private fun registerFallbackService() {
        registerService(
                "fallback",
                UUID.fromString("00000000-0000-0000-0000-000000000000"),
                InetSocketAddress("127.0.0.1", 0)
        )
    }

    private fun registerService(name: String, uniqueId: UUID, socketAddress: InetSocketAddress) {
        val info = ProxyServer.getInstance().constructServerInfo(name, socketAddress,
                uniqueId.toString(), false)
        ProxyServer.getInstance().servers[name] = info
    }

    override fun removeServiceFromProxy(cloudService: ICloudService) {
        ProxyServer.getInstance().servers.remove(cloudService.getName())
    }

    override fun onLoad() {
        ProxyServer.getInstance().reconnectHandler = ReconnectHandlerImpl()
        CloudPlugin(this)
        val synchronizedObjectPromise = CloudAPI.instance.getSingleSynchronizedObjectManager().requestSingleSynchronizedObject("simplecloud-ingamecommands", SynchronizedIngameCommandNamesContainer::class.java)
        synchronizedObjectPromise.addResultListener {
            this.synchronizedIngameCommandNamesContainer = it.obj
        }
    }

    override fun onEnable() {
        ProxyServer.getInstance().configurationAdapter.servers.clear()
        ProxyServer.getInstance().servers.clear()
        for (info in ProxyServer.getInstance().configurationAdapter.listeners) {
            info.serverPriority.clear()
        }

        registerFallbackService()
        CloudPlugin.instance.onEnable()
        CloudAPI.instance.getCloudServiceManager().getAllCachedObjects().forEach { addServiceToProxy(it) }
        CloudAPI.instance.getEventManager().registerListener(CloudPlugin.instance, CloudListener())
        ProxyServer.getInstance().pluginManager.registerListener(this, BungeeListener())
        ProxyServer.getInstance().pluginManager.registerListener(this, IngameCommandListener())

        synchronizeOnlineCountTask()
    }

    override fun onDisable() {
        CloudPlugin.instance.onDisable()
    }

    private fun synchronizeOnlineCountTask() {
        proxy.scheduler.schedule(this, {
            val service = CloudPlugin.instance.thisService()
            service.setOnlineCount(proxy.onlineCount)
            service.update()
        }, 30, 30, TimeUnit.SECONDS)
    }

    override fun getCloudPlayerManagerClass(): KClass<out ICloudPlayerManager> {
        return CloudPlayerManagerBungee::class
    }

}