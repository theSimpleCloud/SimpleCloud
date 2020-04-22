package eu.thesimplecloud.api.sync.`object`

import eu.thesimplecloud.api.CloudAPI

interface ISingleSynchronizedObject : ISynchronizedObject {

    /**
     * Returns the name of this [ISingleSynchronizedObject] used to sync to other components.
     */
    fun getName(): String

    /**
     * Updates this object.
     */
    fun update() {
        CloudAPI.instance.getSingleSynchronizedObjectManager().updateObject(this)
    }

}