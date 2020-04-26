package eu.thesimplecloud.api.eventapi

import eu.thesimplecloud.api.eventapi.exception.EventException

@FunctionalInterface
interface IEventExecutor {

    /**
     * Executes the specified event.
     */
    @Throws(EventException::class)
    fun execute(event: IEvent)

}