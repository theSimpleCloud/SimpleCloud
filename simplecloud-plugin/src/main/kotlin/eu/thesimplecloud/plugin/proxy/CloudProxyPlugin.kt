package eu.thesimplecloud.plugin.proxy

import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.service.ICloudService
import eu.thesimplecloud.lib.service.ServiceState
import eu.thesimplecloud.lib.servicegroup.grouptype.ICloudServerGroup
import eu.thesimplecloud.plugin.startup.CloudPlugin
import eu.thesimplecloud.plugin.listener.CloudServiceUpdateListener
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.plugin.Plugin
import java.net.InetSocketAddress
import java.util.concurrent.TimeUnit

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
        val socketAddress = InetSocketAddress(cloudService.getHost(), cloudService.getPort())
        val info = ProxyServer.getInstance().constructServerInfo(cloudService.getName(), socketAddress,
                cloudService.getUniqueId().toString(), false)
        ProxyServer.getInstance().servers[cloudService.getName()] = info
    }

    override fun onLoad() {
        CloudPlugin(this)
    }

    override fun onEnable() {
        CloudPlugin.instance.enable()
        CloudLib.instance.getEventManager().registerListener(this, CloudServiceUpdateListener())
        ProxyServer.getInstance().servers.clear()
        for (info in ProxyServer.getInstance().configurationAdapter.listeners) {
            info.serverPriority.clear()
        }
        ProxyServer.getInstance().configurationAdapter.servers.clear()
    }

}