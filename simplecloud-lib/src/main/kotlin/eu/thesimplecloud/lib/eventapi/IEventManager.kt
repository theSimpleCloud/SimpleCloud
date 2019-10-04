package eu.thesimplecloud.lib.eventapi

interface IEventManager {

    /**
     * Registers all methods from the specified object that has the [CloudEventHandler] annotation.
     *
     * @param listener the listener object from which the methods should be registered.
     */
    fun registerListener(listener: IListener)

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
     * Unregisters all listeners.
     */
    fun unregisterAll()

}