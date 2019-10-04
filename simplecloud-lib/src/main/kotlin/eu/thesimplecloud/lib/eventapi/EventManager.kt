package eu.thesimplecloud.lib.eventapi

import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.util.ArrayList
import java.util.HashMap

class EventManager : IEventManager {

    /**
     * The map with all [IEvent]s and the listener methods.
     */
    private val listeners = HashMap<Class<out IEvent>, MutableList<MethodData>>()

    override fun registerListener(listener: IListener) {
        for (method in getValidMethods(listener::class.java)) {
            addMethodData(method.parameterTypes[0] as Class<out IEvent>, MethodData(listener, method))
        }
    }

    override fun unregisterListener(listener: IListener) {
        for (method in getValidMethods(listener::class.java)) {
            removeMethodData(method.parameterTypes[0] as Class<out IEvent>, method)
        }
    }

    override fun call(event: IEvent) {
        if (!listeners.containsKey(event::class.java))
            return
        this.listeners[event::class.java]?.forEach { methodData ->
            methodData.method.invoke(methodData.listener, event)
        }
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
     * Adds the [MethodData] to the listeners map.
     *
     * @param eventClass the event class of the method parameter.
     * @param methodData the [MethodData] that should be registered.
     */
    private fun addMethodData(eventClass: Class<out IEvent>, methodData: MethodData) {
        if (this.listeners.containsKey(eventClass)) {
            this.listeners[eventClass]?.add(methodData)
            return
        }
        this.listeners[eventClass] = mutableListOf(methodData)
    }

    /**
     * Removes the [MethodData] to the listeners map.
     *
     * @param parameterType the event class of the method parameter.
     * @param method the [MethodData] that should be unregistered.
     */
    private fun removeMethodData(parameterType: Class<out IEvent>, method: Method) {
        this.listeners[parameterType]?.removeIf { methodData -> methodData.method == method }
    }

    data class MethodData(val listener: IListener, val method: Method)

}