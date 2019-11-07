package eu.thesimplecloud.plugin.server

import com.sun.media.jfxmediaimpl.platform.java.JavaPlatform
import eu.thesimplecloud.plugin.startup.CloudPlugin
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class CloudServerPlugin : JavaPlugin(), ICloudServerPlugin {

    override fun onLoad() {
        CloudPlugin(this)
    }

    override fun onEnable() {
        CloudPlugin.instance.enable()
    }

    override fun shutdown() {
        Bukkit.getServer().shutdown()
    }
}