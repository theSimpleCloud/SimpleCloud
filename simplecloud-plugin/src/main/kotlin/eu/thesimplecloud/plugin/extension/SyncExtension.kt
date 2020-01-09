package eu.thesimplecloud.plugin.extension

import eu.thesimplecloud.plugin.server.CloudSpigotPlugin
import net.md_5.bungee.api.ProxyServer
import org.bukkit.Bukkit

fun syncBukkit(function: () -> Unit) = Bukkit.getScheduler().runTask(CloudSpigotPlugin.instance, function)