package eu.thesimplecloud.api.event.sync.`object`

import eu.thesimplecloud.api.eventapi.IEvent
import eu.thesimplecloud.api.sync.`object`.SynchronizedObjectHolder

class SynchronizedObjectUpdatedEvent(val synchronizedObject: SynchronizedObjectHolder<*>) : IEvent