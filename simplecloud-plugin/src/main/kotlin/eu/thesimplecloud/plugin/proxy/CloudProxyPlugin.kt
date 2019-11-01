package eu.thesimplecloud.plugin.proxy

import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.service.ICloudService
import eu.thesimplecloud.lib.service.ServiceState
import eu.thesimplecloud.plugin.startup.CloudPlugin
import eu.thesimplecloud.plugin.listener.CloudServiceUpdateListener
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.plugin.Plugin
import java.util.concurrent.TimeUnit

class CloudProxyPlugin : Plugin(), ICloudProxyPlugin {

    override fun addServiceToProxy(cloudService: ICloudService) {
    }

    override fun onEnable() {
        CloudLib.instance.getEventManager().registerListener(this, CloudServiceUpdateListener())
        ProxyServer.getInstance().scheduler.schedule(this, {
            if (CloudPlugin.instance.getThisService().getServiceState() === ServiceState.STARTING)
                CloudPlugin.getInstance().getThisService().setServiceState(ServiceState.LOBBY)
            CloudPlugin.getInstance().updateThisService()
        }, 500, TimeUnit.MILLISECONDS)

        ProxyServer.getInstance().servers.clear()
        for (info in ProxyServer.getInstance().configurationAdapter.listeners) {
            info.serverPriority.clear()
        }
        ProxyServer.getInstance().configurationAdapter.servers.clear()
    }

}