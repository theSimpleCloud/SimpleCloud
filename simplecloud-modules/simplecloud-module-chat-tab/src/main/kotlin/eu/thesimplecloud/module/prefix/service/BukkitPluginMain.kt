package eu.thesimplecloud.module.prefix.service

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.module.prefix.service.listener.ChatListener
import eu.thesimplecloud.module.prefix.service.listener.CloudListener
import eu.thesimplecloud.module.prefix.service.listener.JoinListener
import eu.thesimplecloud.module.prefix.service.tablist.TablistHelper
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 19.12.2020
 * Time: 13:34
 */
class BukkitPluginMain : JavaPlugin() {

    override fun onEnable() {
        TablistHelper.load()
        Bukkit.getPluginManager().registerEvents(JoinListener(), this)
        Bukkit.getPluginManager().registerEvents(ChatListener(), this)

        CloudAPI.instance.getEventManager().registerListener(CloudAPI.instance.getThisSidesCloudModule(), CloudListener())
    }

}