/*
 * MIT License
 *
 * Copyright (C) 2020 The SimpleCloud authors
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

package eu.thesimplecloud.api.sync.`object`

import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

interface ISingleSynchronizedObjectManager {

    /**
     * Updates a synchronized object.
     * @return the [SynchronizedObjectHolder] of the specified [synchronizedObject]
     */
    fun <T : ISingleSynchronizedObject> updateObject(synchronizedObject: T, fromPacket: Boolean = false): SynchronizedObjectHolder<T>

    /**
     * Returns the [ISingleSynchronizedObject] found by the specified [name] or null if no object was found.
     */
    fun <T : ISingleSynchronizedObject> getObject(name: String): SynchronizedObjectHolder<T>?

    /**
     * Returns a [ICommunicationPromise] that completes once the requested [ISingleSynchronizedObject] is available.
     */
    fun <T : ISingleSynchronizedObject> requestSingleSynchronizedObject(name: String, clazz: Class<T>): ICommunicationPromise<SynchronizedObjectHolder<T>>

}