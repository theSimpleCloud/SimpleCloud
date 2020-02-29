package eu.thesimplecloud.api.event.sync.`object`

import eu.thesimplecloud.api.eventapi.IEvent
import eu.thesimplecloud.api.sync.`object`.ISynchronizedObject

class SynchronizedObjectUpdatedEvent(val synchronizedObject: ISynchronizedObject) : IEvent