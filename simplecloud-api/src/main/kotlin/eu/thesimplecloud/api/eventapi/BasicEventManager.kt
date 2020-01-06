package eu.thesimplecloud.api.eventapi

import eu.thesimplecloud.api.external.ICloudModule
import java.lang.reflect.Method
import java.util.HashMap

open class BasicEventManager : IEventManager {

    /**
     * The map with all [IEvent]s and the listener methods.
     */
    private val listeners = HashMap<Class<out IEvent>, MutableList<EventData>>()

    override fun registerListener(cloudModule: ICloudModule, listener: IListener) {
        for (method in getValidMethods(listener::class.java)) {
            addMethodData(method.parameterTypes[0] as Class<out IEvent>, EventData(cloudModule, listener, method))
        }
    }

    override fun unregisterListener(listener: IListener) {
        for (method in getValidMethods(listener::class.java)) {
            removeMethodData(method.parameterTypes[0] as Class<out IEvent>, method)
        }
    }

    override fun call(event: IEvent, fromPacket: Boolean) {
        this.listeners[event::class.java]?.forEach { methodData ->
            methodData.method.invoke(methodData.listener, event)
        }
    }

    override fun unregisterAllListenersByCloudModule(cloudModule: ICloudModule) {
        listeners.values.forEach { list -> list.removeIf { it.cloudModule == cloudModule } }
    }

    override fun unregisterAll() {
        this.listeners.clear()
    }

    /**
     * Gets all methods that have the [CloudEventHandler] annotation and only one parameter
     *
     * @param listenerClass
     * @return
     */
    private fun getValidMethods(listenerClass: Class<out IListener>): List<Method> {
        val methods = listenerClass.declaredMethods
                .filter { it.isAnnotationPresent(CloudEventHandler::class.java) && it.parameterTypes.size == 1 && IEvent::class.java.isAssignableFrom(it.parameterTypes[0]) }
        methods.forEach { it.isAccessible = true }
        return methods
    }

    /**
     * Adds the [EventData] to the listeners map.
     *
     * @param eventClass the event class of the method parameter.
     * @param eventData the [EventData] that should be registered.
     */
    private fun addMethodData(eventClass: Class<out IEvent>, eventData: EventData) {
        this.listeners.getOrPut(eventClass, { ArrayList() }).add(eventData)
    }

    /**
     * Removes the [EventData] to the listeners map.
     *
     * @param parameterType the event class of the method parameter.
     * @param method the [EventData] that should be unregistered.
     */
    private fun removeMethodData(parameterType: Class<out IEvent>, method: Method) {
        this.listeners[parameterType]?.removeIf { methodData -> methodData.method == method }
    }

    data class EventData(val cloudModule: ICloudModule, val listener: IListener, val method: Method)

}