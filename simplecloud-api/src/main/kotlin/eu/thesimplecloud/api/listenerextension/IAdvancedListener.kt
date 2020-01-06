package eu.thesimplecloud.api.listenerextension

import java.util.function.Predicate

interface IAdvancedListener<T> {

    fun addCondition(predicate: (T) -> Boolean)

    fun addAction(function: (T) -> Unit)

    fun unregisterWhen(advancedListener: IAdvancedListener<*>)

    fun unregister()

}