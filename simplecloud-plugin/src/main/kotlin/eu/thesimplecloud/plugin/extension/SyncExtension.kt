package eu.thesimplecloud.plugin.extension

import eu.thesimplecloud.plugin.proxy.bungee.CloudBungeePlugin
import eu.thesimplecloud.plugin.proxy.ICloudProxyPlugin
import eu.thesimplecloud.plugin.server.CloudSpigotPlugin
import eu.thesimplecloud.plugin.startup.CloudPlugin
import net.md_5.bungee.api.ProxyServer
import org.bukkit.Bukkit
import java.util.concurrent.TimeUnit

fun syncBukkit(function: () -> Unit) = Bukkit.getScheduler().runTask(CloudSpigotPlugin.instance, function)

fun syncService(function: () -> Unit) {
    if (CloudPlugin.instance.cloudServicePlugin is ICloudProxyPlugin) {
        ProxyServer.getInstance().scheduler.schedule(CloudBungeePlugin.instance, function, 0, TimeUnit.MILLISECONDS)
    } else {
        Bukkit.getScheduler().runTask(CloudSpigotPlugin.instance, function)
    }
}