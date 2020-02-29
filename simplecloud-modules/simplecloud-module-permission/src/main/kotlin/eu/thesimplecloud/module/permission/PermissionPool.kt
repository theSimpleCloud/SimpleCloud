package eu.thesimplecloud.module.permission

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.event.sync.list.SynchronizedListObjectRemovedEvent
import eu.thesimplecloud.api.eventapi.CloudEventHandler
import eu.thesimplecloud.api.eventapi.IListener
import eu.thesimplecloud.api.event.sync.list.SynchronizedListObjectUpdatedEvent
import eu.thesimplecloud.base.manager.startup.Manager
import eu.thesimplecloud.module.permission.core.TestSynchronizedListObject
import eu.thesimplecloud.module.permission.core.TestSynchronizedObjectList
import eu.thesimplecloud.plugin.startup.CloudPlugin

class PermissionPool : IPermissionPool {

    init {
        CloudAPI.instance.getSynchronizedObjectListManager().registerSynchronizedObjectList(TestSynchronizedObjectList())
        val cloudModule = if (CloudAPI.instance.isManager()) Manager.instance else CloudPlugin.instance
        CloudAPI.instance.getEventManager().registerListener(cloudModule, object : IListener {

            @CloudEventHandler
            fun on(event: SynchronizedListObjectUpdatedEvent) {
                println("updated list obj: " + (event.obj as TestSynchronizedListObject))
                println(CloudAPI.instance.getSynchronizedObjectListManager().getSynchronizedObjectList("test")?.getAllCachedObjects())
            }

            @CloudEventHandler
            fun on(event: SynchronizedListObjectRemovedEvent) {
                println("removed list obj: " + (event.obj as TestSynchronizedListObject))
                println("list objects: " + CloudAPI.instance.getSynchronizedObjectListManager().getSynchronizedObjectList("test")?.getAllCachedObjects())
            }

        })
    }

}