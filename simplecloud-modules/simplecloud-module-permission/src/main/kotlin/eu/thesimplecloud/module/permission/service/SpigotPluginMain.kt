package eu.thesimplecloud.module.permission.service

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.event.syncobject.SynchronizedObjectUpdatedEvent
import eu.thesimplecloud.api.eventapi.CloudEventHandler
import eu.thesimplecloud.api.eventapi.IListener
import eu.thesimplecloud.module.permission.TestSynchronizedObject
import eu.thesimplecloud.module.permission.event.TestSynchronizedEvent
import eu.thesimplecloud.plugin.startup.CloudPlugin
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class SpigotPluginMain : JavaPlugin() {

    lateinit var testObject: TestSynchronizedObject

    override fun onEnable() {


        CloudAPI.instance.getEventManager().registerListener(CloudPlugin.instance, object: IListener {

            @CloudEventHandler
            fun on(event: SynchronizedObjectUpdatedEvent) {
            }

        })
        testObject = CloudAPI.instance.getSynchronizedObjectManager().requestSynchronizedObject("test12", TestSynchronizedObject::class.java).awaitUninterruptibly().getNow()

    }

}