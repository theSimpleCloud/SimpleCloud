package eu.thesimplecloud.api.event.sync.list

import eu.thesimplecloud.api.eventapi.IEvent
import eu.thesimplecloud.api.sync.list.ISynchronizedListObject

class SynchronizedListObjectUpdatedEvent(val obj: ISynchronizedListObject) : IEvent