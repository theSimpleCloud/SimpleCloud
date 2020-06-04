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

package eu.thesimplecloud.api.sync.`object`

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.event.sync.`object`.SynchronizedObjectUpdatedEvent
import java.util.concurrent.ConcurrentHashMap

abstract class AbstractSingleSynchronizedObjectManager : ISingleSynchronizedObjectManager {

    protected val nameToValue: MutableMap<String, SynchronizedObjectHolder<ISingleSynchronizedObject>> = ConcurrentHashMap()

    override fun <T : ISingleSynchronizedObject> updateObject(synchronizedObject: T, fromPacket: Boolean): SynchronizedObjectHolder<T> {
        if (nameToValue.containsKey(synchronizedObject.getName())) {
            val cachedHolder = nameToValue[synchronizedObject.getName()]!!
            if (cachedHolder.obj !== synchronizedObject) {
                if (cachedHolder.obj::class.java.name != synchronizedObject::class.java.name) throw IllegalArgumentException("Class of registered value by name ${synchronizedObject.getName()} is not matching the class of the update value. Registered: ${cachedHolder::class.java.name} Update: ${synchronizedObject::class.java.name}")
                cachedHolder.obj = synchronizedObject
            }
            CloudAPI.instance.getEventManager().call(SynchronizedObjectUpdatedEvent(cachedHolder))
            return cachedHolder as SynchronizedObjectHolder<T>
        } else {
            val objectHolder = SynchronizedObjectHolder(synchronizedObject)
            nameToValue[synchronizedObject.getName()] = objectHolder as SynchronizedObjectHolder<ISingleSynchronizedObject>
            CloudAPI.instance.getEventManager().call(SynchronizedObjectUpdatedEvent(objectHolder))
            return objectHolder
        }
    }

    override fun <T : ISingleSynchronizedObject> getObject(name: String): SynchronizedObjectHolder<T>? {
        return nameToValue[name] as SynchronizedObjectHolder<T>?
    }

}