package eu.thesimplecloud.plugin.server

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.player.ICloudPlayerManager
import eu.thesimplecloud.plugin.impl.player.CloudPlayerManagerSpigot
import eu.thesimplecloud.plugin.listener.CloudListener
import eu.thesimplecloud.plugin.server.listener.SpigotListener
import eu.thesimplecloud.plugin.startup.CloudPlugin
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import kotlin.reflect.KClass

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
        synchronizeOnlineCountTask()
    }

    override fun onDisable() {
        CloudPlugin.instance.onDisable()
    }

    override fun shutdown() {
        Bukkit.getServer().shutdown()
    }

    override fun getCloudPlayerManagerClass(): KClass<out ICloudPlayerManager> {
        return CloudPlayerManagerSpigot::class
    }

    private fun synchronizeOnlineCountTask() {
        object : BukkitRunnable() {
            override fun run() {
                val service = CloudPlugin.instance.thisService()
                service.setOnlineCount(server.onlinePlayers.size)
                service.update()
            }
        }.runTaskTimerAsynchronously(this, 20 * 30, 20 * 30)
    }

}