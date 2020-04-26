package eu.thesimplecloud.api.eventapi.exception

import eu.thesimplecloud.api.eventapi.IEvent

class EventException(causeEvent: IEvent, cause: Throwable) : Exception("An error occurred while attempting to handle event ${causeEvent::class.java.name}", cause) {
}