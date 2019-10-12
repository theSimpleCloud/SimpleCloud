package eu.thesimplecloud.lib.eventapi

import eu.thesimplecloud.lib.external.ICloudModule

interface IEventManager {

    /**
     * Registers all methods from the specified object that has the [CloudEventHandler] annotation.
     *
     * @param listener the listener object from which the methods should be registered.
     */
    fun registerListener(cloudModule: ICloudModule, listener: IListener)

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
     */
    fun call(event: IEvent)

    /**
     * Unregisters all listeners registered with the specified [ICloudModule].
     */
    fun unregisterAllListenersByCloudModule(cloudModule: ICloudModule)

    /**
     * Unregisters all listeners.
     */
    fun unregisterAll()

}