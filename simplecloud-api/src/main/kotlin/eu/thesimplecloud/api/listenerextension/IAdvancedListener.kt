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

import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

interface IAdvancedListener<T : Any> {

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
    fun toPromise(): ICommunicationPromise<T>

    /**
     * Returns a promise that completes when the listener was called.
     */
    fun toUnitPromise(): ICommunicationPromise<Unit>

}