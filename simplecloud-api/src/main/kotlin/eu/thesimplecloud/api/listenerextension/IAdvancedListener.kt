package eu.thesimplecloud.api.listenerextension

import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

interface IAdvancedListener<T> {

    /**
     * Adds a condition to this listener
     * @return this listener
     */
    fun addCondition(predicate: (T) -> Boolean): IAdvancedListener<T>

    /**
     * Adds an action to this listener
     * @return this listener
     */
    fun addAction(function: (T) -> Unit): IAdvancedListener<T>

    /**
     * Unregisters this listener when the specified listener is called
     * @return this listener
     */
    fun unregisterWhen(advancedListener: IAdvancedListener<*>): IAdvancedListener<T>

    /**
     * Unregisters this listener
     */
    fun unregister()

    /**
     * Unregisters this listener after one call.
     * @return this listener
     */
    fun unregisterAfterCall(): IAdvancedListener<T>

    /**
     * Returns a promise that completes when the listener was called.
     */
    fun toPromise(): ICommunicationPromise<Unit>

}