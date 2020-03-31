package eu.thesimplecloud.api.listenerextension

import eu.thesimplecloud.api.eventapi.BasicEventManager
import eu.thesimplecloud.api.eventapi.CloudEventHandler
import eu.thesimplecloud.api.eventapi.IEvent
import eu.thesimplecloud.api.eventapi.IListener
import eu.thesimplecloud.api.external.ICloudModule

class AdvancedListener<T : IEvent>(cloudModule: ICloudModule, val eventManager: BasicEventManager) : IAdvancedListener<T> {

    private val conditions = ArrayList<(T) -> Boolean>()
    private val actions = ArrayList<(T) -> Unit>()

    init {
        eventManager.registerListener(cloudModule, object : IListener {

            @CloudEventHandler
            fun on(event: T) {
                println(event::class.java.simpleName)
            }

        })
    }

    override fun addCondition(predicate: (T) -> Boolean) {
        this.conditions.add(predicate)
    }

    override fun addAction(function: (T) -> Unit) {
        this.actions.add(function)
    }

    override fun unregisterWhen(advancedListener: IAdvancedListener<*>) {
    }

    override fun unregister() {
    }


}