package eu.thesimplecloud.api.eventapi

/**
 * A special type of [IEvent]
 * Synchronized events will be sent to all connected components(Wrappers and services). Because of this they must not contain any unserializable member variables like interfaces.
 */
interface ISynchronizedEvent : IEvent