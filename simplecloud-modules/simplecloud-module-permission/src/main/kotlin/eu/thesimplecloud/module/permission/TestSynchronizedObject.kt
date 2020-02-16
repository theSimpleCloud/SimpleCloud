package eu.thesimplecloud.module.permission

import eu.thesimplecloud.api.syncobject.ISynchronizedObject

class TestSynchronizedObject(var test: Int) : ISynchronizedObject {


    override fun getName(): String {
        return "test12"
    }
}