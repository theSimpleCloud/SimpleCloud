package eu.thesimplecloud.api.listenerextension

import eu.thesimplecloud.api.eventapi.IEvent

inline fun <reified T : IEvent> cloudListener(autoUnregister: Boolean = true, unregisterTimeInSeconds: Long = 5 * 60): AdvancedListener<T> {
    return AdvancedListener(T::class.java, autoUnregister, unregisterTimeInSeconds)
}