package eu.thesimplecloud.plugin.proxy

import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.service.ICloudService
import eu.thesimplecloud.lib.service.ServiceState
import eu.thesimplecloud.lib.servicegroup.grouptype.ICloudServerGroup
import eu.thesimplecloud.plugin.listener.CloudServiceUpdateListener
import eu.thesimplecloud.plugin.proxy.listener.BungeeListener
import eu.thesimplecloud.plugin.startup.CloudPlugin
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.plugin.Plugin
import java.net.InetSocketAddress
import java.net.URLClassLoader

class CloudProxyPlugin : Plugin(), ICloudProxyPlugin {

    override fun shutdown() {
        ProxyServer.getInstance().stop()
    }

    override fun addServiceToProxy(cloudService: ICloudService) {
        val serviceState = cloudService.getState()
        if (cloudService.getServiceType().isProxy() || serviceState == ServiceState.CLOSED || serviceState == ServiceState.PREPARED)
            return
        val cloudServiceGroup = cloudService.getServiceGroup()
        if ((cloudServiceGroup as ICloudServerGroup).getHiddenAtProxyGroups().contains(CloudPlugin.instance.getGroupName()))
            return
        println("Registered service ${cloudService.getName()}")
        val socketAddress = InetSocketAddress(cloudService.getHost(), cloudService.getPort())
        val info = ProxyServer.getInstance().constructServerInfo(cloudService.getName(), socketAddress,
                cloudService.getUniqueId().toString(), false)
        ProxyServer.getInstance().servers[cloudService.getName()] = info
    }

    override fun onLoad() {
        ProxyServer.getInstance().reconnectHandler = ReconnectHandlerImpl()
        val classLoader = URLClassLoader(arrayOf(this.file.toURI().toURL()))
        CloudPlugin(this, classLoader)
    }

    override fun onEnable() {
        ProxyServer.getInstance().servers.clear()
        for (info in ProxyServer.getInstance().configurationAdapter.listeners) {
            info.serverPriority.clear()
        }
        ProxyServer.getInstance().configurationAdapter.servers.clear()
        CloudPlugin.instance.enable()
        CloudLib.instance.getEventManager().registerListener(this, CloudServiceUpdateListener())
        ProxyServer.getInstance().pluginManager.registerListener(this, BungeeListener())

    }

}