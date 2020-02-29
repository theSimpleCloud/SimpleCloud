package eu.thesimplecloud.api.sync.`object`

import eu.thesimplecloud.api.CloudAPI

interface ISynchronizedObject {

    /**
     * Returns the name of this [ISynchronizedObject] used to sync to other components.
     */
    fun getName(): String

    /**
     * Updates this object.
     */
    fun update() {
        CloudAPI.instance.getSynchronizedObjectManager().updateObject(this)
    }

}