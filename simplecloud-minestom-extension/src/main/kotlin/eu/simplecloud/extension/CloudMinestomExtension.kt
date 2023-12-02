package eu.simplecloud.extension

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.player.ICloudPlayerManager
import eu.simplecloud.extension.listener.MinestomListener
import eu.simplecloud.extension.impl.player.CloudPlayerManagerMinestom
import eu.thesimplecloud.plugin.listener.CloudListener
import eu.thesimplecloud.plugin.server.ICloudServerPlugin
import eu.thesimplecloud.plugin.startup.CloudPlugin
import net.minestom.server.MinecraftServer
import net.minestom.server.extensions.Extension
import java.time.Duration
import kotlin.reflect.KClass

class CloudMinestomExtension : Extension(), ICloudServerPlugin {

    companion object {
        @JvmStatic
        lateinit var instance: CloudMinestomExtension
    }

    init {
        instance = this
    }

    override fun preInitialize() {
        CloudPlugin(this)
    }

    override fun initialize() {
        CloudPlugin.instance.onEnable()
        CloudAPI.instance.getEventManager().registerListener(CloudPlugin.instance, CloudListener())
        MinestomListener.register(MinecraftServer.getGlobalEventHandler())
        synchronizeOnlineCountTask()
    }

    override fun terminate() {
        CloudPlugin.instance.onDisable()
    }

    override fun getCloudPlayerManagerClass(): KClass<out ICloudPlayerManager> {
        return CloudPlayerManagerMinestom::class
    }

    override fun shutdown() {
        MinecraftServer.getServer().stop()
    }

    private fun synchronizeOnlineCountTask() {
        MinecraftServer.getSchedulerManager().buildTask {
            val service = CloudPlugin.instance.thisService()
            val onlinePlayers = MinecraftServer.getConnectionManager().onlinePlayers
            val onlinePlayerCount = onlinePlayers.size

            if (service.getOnlineCount() != onlinePlayerCount) {
                service.setOnlineCount(onlinePlayerCount)
                service.update()
            }
        }
            .delay(Duration.ofSeconds(30))
            .repeat(Duration.ofSeconds(30))
            .schedule()
    }
}