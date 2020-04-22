package eu.thesimplecloud.api.eventapi

import com.google.common.collect.Maps
import eu.thesimplecloud.api.eventapi.exception.EventException
import eu.thesimplecloud.api.external.ICloudModule
import java.lang.reflect.Method
import java.util.concurrent.CopyOnWriteArrayList

open class BasicEventManager : IEventManager {

    /**
     * The map with all [IEvent]s and the listener methods.
     */
    private val listeners = Maps.newConcurrentMap<Class<out IEvent>, MutableList<RegisteredEvent>>()

    override fun registerListener(cloudModule: ICloudModule, listener: IListener) {
        for (method in getValidMethods(listener::class.java)) {
            val eventClass = method.parameterTypes[0] as Class<out IEvent>
            addRegisteredEvent(RegisteredEvent.fromEventMethod(cloudModule, eventClass, listener, method))
        }
    }

    override fun registerEvent(cloudModule: ICloudModule, eventClass: Class<out IEvent>, listener: IListener, eventExecutor: IEventExecutor) {
        addRegisteredEvent(RegisteredEvent(cloudModule, eventClass, listener, eventExecutor))
    }

    override fun unregisterListener(listener: IListener) {
        val list = this.listeners.values.map { it.filter { it.listener == listener } }.flatten()
        list.forEach { removeRegisteredEvent(it) }
    }

    override fun call(event: IEvent, fromPacket: Boolean) {
        this.listeners[event::class.java]?.forEach { registeredEvent ->
            registeredEvent.eventExecutor.execute(event)
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
     * Adds the [RegisteredEvent] to the listeners map.
     *
     * @param registeredEvent the [RegisteredEvent] that should be registered.
     */
    private fun addRegisteredEvent(registeredEvent: RegisteredEvent) {
        this.listeners.getOrPut(registeredEvent.eventClass, { CopyOnWriteArrayList() }).add(registeredEvent)
    }

    /**
     * Removes the [RegisteredEvent] from the listeners map.
     *
     * @param registeredEvent the [RegisteredEvent] that should be removed.
     */
    private fun removeRegisteredEvent(registeredEvent: RegisteredEvent) {
        this.listeners[registeredEvent.eventClass]?.remove(registeredEvent)
    }

    data class RegisteredEvent(val cloudModule: ICloudModule, val eventClass: Class<out IEvent>, val listener: IListener, val eventExecutor: IEventExecutor) {

        companion object {
            fun fromEventMethod(cloudModule: ICloudModule, eventClass: Class<out IEvent>, listener: IListener, method: Method): RegisteredEvent {
                return RegisteredEvent(cloudModule, eventClass, listener, object : IEventExecutor {

                    override fun execute(event: IEvent) {
                        if (!eventClass.isAssignableFrom(event.javaClass))
                            return
                        try {
                            method.invoke(listener, event)
                        } catch (ex: Exception) {
                            throw EventException(event, ex)
                        }
                    }
                })
            }
        }

    }

}