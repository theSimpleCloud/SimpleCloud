/*
 * MIT License
 *
 * Copyright (C) 2020-2022 The SimpleCloud authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package eu.thesimplecloud.api.listenerextension

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.eventapi.IEvent
import eu.thesimplecloud.api.eventapi.IEventExecutor
import eu.thesimplecloud.api.eventapi.IListener
import eu.thesimplecloud.clientserverapi.lib.promise.CommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import io.netty.util.concurrent.GlobalEventExecutor
import java.util.concurrent.TimeUnit

open class AdvancedListener<T : IEvent>(
    eventClass: Class<out IEvent>,
    autoUnregister: Boolean = true,
    unregisterTimeInSeconds: Long = 5 * 60
) : IAdvancedListener<T> {

    private val conditions = ArrayList<(T) -> Boolean>()
    private val actions = ArrayList<(T) -> Unit>()
    private val listenerObj = object : IListener {}

    init {

        CloudAPI.instance.getEventManager().registerEvent(
            CloudAPI.instance.getThisSidesCloudModule(),
            eventClass,
            listenerObj, object : IEventExecutor {
                override fun execute(event: IEvent) {
                    if (!eventClass.isAssignableFrom(event.javaClass))
                        return
                    event as T
                    if (conditions.all { it(event) }) actions.forEach { it(event) }
                }
            })
        if (autoUnregister) {
            GlobalEventExecutor.INSTANCE.schedule({
                this.unregister()
            }, unregisterTimeInSeconds, TimeUnit.SECONDS)
        }
    }

    override fun addCondition(predicate: (T) -> Boolean): IAdvancedListener<T> {
        this.conditions.add(predicate)
        return this
    }

    override fun addAction(function: (T) -> Unit): IAdvancedListener<T> {
        this.actions.add(function)
        return this
    }

    override fun unregisterWhen(advancedListener: IAdvancedListener<*>): IAdvancedListener<T> {
        advancedListener.addAction { this.unregister() }
        return this
    }

    override fun unregister() {
        CloudAPI.instance.getEventManager().unregisterListener(listenerObj)
    }

    override fun unregisterAfterCall(): IAdvancedListener<T> {
        this.addAction { this.unregister() }
        return this
    }

    override fun toPromise(): ICommunicationPromise<T> {
        val newPromise = CommunicationPromise<T>(enableTimeout = false)
        this.addAction { newPromise.trySuccess(it) }
        this.unregisterAfterCall()
        return newPromise
    }

    override fun toUnitPromise(): ICommunicationPromise<Unit> {
        val newPromise = CommunicationPromise<Unit>(enableTimeout = false)
        this.addAction { newPromise.trySuccess(Unit) }
        this.unregisterAfterCall()
        return newPromise
    }


}