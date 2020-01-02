package eu.thesimplecloud.plugin.server

import com.sun.media.jfxmediaimpl.platform.java.JavaPlatform
import eu.thesimplecloud.plugin.startup.CloudPlugin
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.net.URLClassLoader

class CloudSpigotPlugin : JavaPlugin(), ICloudServerPlugin {

    override fun onLoad() {
        CloudPlugin(this, URLClassLoader(arrayOf(this.file.toURI().toURL())))
    }

    override fun onEnable() {
        CloudPlugin.instance.enable()
    }

    override fun shutdown() {
        Bukkit.getServer().shutdown()
    }
}