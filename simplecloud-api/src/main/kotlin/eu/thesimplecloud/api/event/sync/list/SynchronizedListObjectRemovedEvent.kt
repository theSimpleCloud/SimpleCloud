package eu.thesimplecloud.api.event.sync.list

import eu.thesimplecloud.api.eventapi.IEvent
import eu.thesimplecloud.api.sync.list.ISynchronizedListObject

class SynchronizedListObjectRemovedEvent(val obj: ISynchronizedListObject) : IEvent