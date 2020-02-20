package eu.thesimplecloud.plugin.server

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.plugin.listener.CloudListener
import eu.thesimplecloud.plugin.server.listener.SpigotListener
import eu.thesimplecloud.plugin.startup.CloudPlugin
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class CloudSpigotPlugin : JavaPlugin(), ICloudServerPlugin {

    companion object {
        @JvmStatic
        lateinit var instance: CloudSpigotPlugin
    }

    init {
        instance = this
    }

    override fun onLoad() {
        CloudPlugin(this)
    }

    override fun onEnable() {
        CloudPlugin.instance.onEnable()
        CloudAPI.instance.getEventManager().registerListener(CloudPlugin.instance, CloudListener())
        server.pluginManager.registerEvents(SpigotListener(), this)
    }

    override fun onDisable() {
        CloudPlugin.instance.onDisable()
    }

    override fun shutdown() {
        Bukkit.getServer().shutdown()
    }
}