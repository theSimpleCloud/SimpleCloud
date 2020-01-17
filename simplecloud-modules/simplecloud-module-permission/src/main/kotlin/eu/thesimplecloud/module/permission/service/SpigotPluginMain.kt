package eu.thesimplecloud.module.permission.service

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.eventapi.CloudEventHandler
import eu.thesimplecloud.api.eventapi.IListener
import eu.thesimplecloud.module.permission.event.TestSynchronizedEvent
import eu.thesimplecloud.plugin.startup.CloudPlugin
import org.bukkit.plugin.java.JavaPlugin

class SpigotPluginMain : JavaPlugin() {

    override fun onEnable() {
        CloudAPI.instance.getEventManager().registerListener(CloudPlugin.instance, object: IListener {

            @CloudEventHandler
            fun on(event: TestSynchronizedEvent) {
                println("called. ClassLoader: ${CloudPlugin.instance.classLoader == event::class.java.classLoader}")
                println("called. ClassLoader: ${CloudPlugin.instance.classLoader == this::class.java.classLoader}")
            }

        })
        CloudAPI.instance.getEventManager().call(TestSynchronizedEvent("Name"))
    }

}