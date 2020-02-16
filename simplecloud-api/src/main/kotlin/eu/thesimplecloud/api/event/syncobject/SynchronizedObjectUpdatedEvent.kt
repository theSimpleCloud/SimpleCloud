package eu.thesimplecloud.api.event.syncobject

import eu.thesimplecloud.api.eventapi.IEvent
import eu.thesimplecloud.api.syncobject.ISynchronizedObject

class SynchronizedObjectUpdatedEvent(val synchronizedObject: ISynchronizedObject) : IEvent