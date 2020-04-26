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