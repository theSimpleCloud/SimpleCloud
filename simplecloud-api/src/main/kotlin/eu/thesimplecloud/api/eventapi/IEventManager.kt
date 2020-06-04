/*
 * MIT License
 *
 * Copyright (C) 2020 SimpleCloud-Team
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

package eu.thesimplecloud.api.eventapi

import eu.thesimplecloud.api.eventapi.exception.EventException
import eu.thesimplecloud.api.external.ICloudModule

interface IEventManager {

    /**
     * Registers all methods from the specified object that has the [CloudEventHandler] annotation.
     *
     * @param listener the listener object from which the methods should be registered.
     */
    fun registerListener(cloudModule: ICloudModule, listener: IListener)

    /**
     * Registers one event.
     */
    fun registerEvent(cloudModule: ICloudModule, eventClass: Class<out IEvent>, listener: IListener, eventExecutor: IEventExecutor)

    /**
     * Unregisters all methods from the specified object that has the [CloudEventHandler] annotation.
     *
     * @param listener the listener object from which the methods should be unregistered.
     */
    fun unregisterListener(listener: IListener)

    /**
     * Calls all methods which were registered.
     *
     * @param event the event which should be called.
     * @param fromPacket whether the event shall be synchronized with all components. Only used when the specified [event] is a [ISynchronizedEvent].
     */
    @Throws(EventException::class)
    fun call(event: IEvent, fromPacket: Boolean = false)

    /**
     * Unregisters all listeners registered with the specified [ICloudModule].
     */
    fun unregisterAllListenersByCloudModule(cloudModule: ICloudModule)

    /**
     * Unregisters all listeners.
     */
    fun unregisterAll()

}