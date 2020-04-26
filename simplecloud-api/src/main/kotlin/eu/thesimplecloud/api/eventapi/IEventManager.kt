package eu.thesimplecloud.api.eventapi

import eu.thesimplecloud.api.eventapi.exception.EventException
import eu.thesimplecloud.api.external.ICloudModule

interface IEventManager {

    /**
     * Registers all methods from the specified object that has the [CloudEventHandler] annotation.
     *
     * @param listener the listener object from which the methods should be registered.
     */
    fun registerListener(cloudModule: ICloudModule, listener: IListener)

    /**
     * Registers one event.
     */
    fun registerEvent(cloudModule: ICloudModule, eventClass: Class<out IEvent>, listener: IListener, eventExecutor: IEventExecutor)

    /**
     * Unregisters all methods from the specified object that has the [CloudEventHandler] annotation.
     *
     * @param listener the listener object from which the methods should be unregistered.
     */
    fun unregisterListener(listener: IListener)

    /**
     * Calls all methods which were registered.
     *
     * @param event the event which should be called.
     * @param fromPacket whether the event shall be synchronized with all components. Only used when the specified [event] is a [ISynchronizedEvent].
     */
    @Throws(EventException::class)
    fun call(event: IEvent, fromPacket: Boolean = false)

    /**
     * Unregisters all listeners registered with the specified [ICloudModule].
     */
    fun unregisterAllListenersByCloudModule(cloudModule: ICloudModule)

    /**
     * Unregisters all listeners.
     */
    fun unregisterAll()

}